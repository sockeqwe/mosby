/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * This class
 * is used to save, restore and apply a {@link ViewState} by using {@link ViewStateSupport}. It's
 * just a little helper / utils class that avoids to many copy & paste code clones.
 * However, you have to hook in the corresponding methods correctly in your Fragment, Activity or
 * android.widget.View
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ViewStateManager<V extends MvpView> {

  private V view;
  private ViewStateSupport viewStateSupport;
  private boolean retainingInstanceState = false;
  private boolean applyViewState = false;

  public ViewStateManager(ViewStateSupport viewStateSupport, V view) {
    this.viewStateSupport = viewStateSupport;
    this.view = view;

    if (viewStateSupport == null) {
      throw new NullPointerException("ViewStateSupport can not be null");
    }

    if (view == null) {
      throw new NullPointerException("View can not be null");
    }
  }

  /**
   * Like the name already suggests. Creates a new viewstate or tries to restore the old one (must
   * be subclass of {@link RestoreableViewState}) by reading the bundle
   *
   * @return true, if the viewstate has been restored (in other words {@link
   * ViewState#apply(MvpView, boolean)} has been invoked) (calls {@link
   * ViewStateSupport#onViewStateInstanceRestored(boolean) after having restored the viewstate}.
   * Otherwise returns false and calls {@link ViewStateSupport#onNewViewStateInstance()}
   */
  public <V extends MvpView> boolean createOrRestoreViewState(Bundle savedInstanceState) {

    // ViewState already exists (Fragment retainsInstanceState == true)
    if (viewStateSupport.getViewState() != null) {
      retainingInstanceState = true;
      applyViewState = true;
      return true;
    }

    // Create view state
    viewStateSupport.setViewState(viewStateSupport.createViewState());
    if (viewStateSupport.getViewState() == null) {
      throw new NullPointerException(
          "ViewState is null! Do you return null in createViewState() method?");
    }

    // Try to restore data from bundle (savedInstanceState)
    if (savedInstanceState != null
        && viewStateSupport.getViewState() instanceof RestoreableViewState) {

      ViewState restoredViewState =
          ((RestoreableViewState) viewStateSupport.getViewState()).restoreInstanceState(
              savedInstanceState);

      boolean restoredFromBundle = restoredViewState != null;

      if (restoredFromBundle) {
        viewStateSupport.setViewState(restoredViewState);
        retainingInstanceState = false;
        applyViewState = true;
        return true;
      }
    }

    // ViewState not restored, activity / fragment starting first time
    applyViewState = false;
    return false;
  }

  /**
   * applies the views state to the view.
   *
   * @return true if viewstate has been applied, otherwise false (i.e. activity / fragment is
   * starting for the first time)
   */
  public boolean applyViewState() {
    if (applyViewState) {
      viewStateSupport.setRestoringViewState(true);
      viewStateSupport.getViewState().apply(view, retainingInstanceState);
      viewStateSupport.setRestoringViewState(false);
      viewStateSupport.onViewStateInstanceRestored(retainingInstanceState);
      return true;
    }

    viewStateSupport.onNewViewStateInstance();
    return false;
  }

  /**
   * Saves {@link RestoreableViewState} in a bundle. <b>Should be calld from activities or
   * fragments
   * onSaveInstanceState(Bundle) method</b>
   */
  public void saveViewState(Bundle outState, boolean retainingInstanceState) {

    if (viewStateSupport == null) {
      throw new NullPointerException("ViewStateSupport can not be null");
    }

    ViewState viewState = viewStateSupport.getViewState();
    if (viewState == null) {
      throw new NullPointerException("ViewState is null! That's not allowed");
    }

    // Save the viewstate
    if (viewState != null && viewState instanceof RestoreableViewState && !retainingInstanceState) {
      ((RestoreableViewState) viewState).saveInstanceState(outState);
    }

    if (retainingInstanceState) {
      // For next time we call applyViewState() we have to set this flag to true
      applyViewState = true;
    }
  }
}
