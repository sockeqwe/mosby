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

package com.hannesdorfmann.mosby.mvp.delegate;

import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.ViewStateSavedState;

/**
 * This class
 * is used to save, restore and apply a {@link ViewState} by using {@link
 * BaseMvpViewStateDelegateCallback}. It's
 * just a little helper / utils class that avoids to many copy & paste code clones.
 * This class is designed to be for custom Layouts like {@link MvpViewStateFrameLayout} etc.
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
class MvpInternalLayoutViewStateDelegate<V extends MvpView, P extends MvpPresenter<V>>
    extends MvpInternalDelegate<V, P> {

  private boolean applyViewState = false;
  private boolean createOrRestoreCalled = false;

  MvpInternalLayoutViewStateDelegate(MvpViewStateViewGroupDelegateCallback<V, P> delegateCallback) {
    super(delegateCallback);
  }

  /**
   * Like the name already suggests. Creates a new viewstate or tries to restore the old one (must
   * be subclass of {@link RestorableViewState}) by reading the bundle
   *
   * @return true, if the viewstate has been restored (in other words restored from parcelable)
   * (calls {@link
   * BaseMvpViewStateDelegateCallback#onViewStateInstanceRestored(boolean) after having restored the
   * viewstate}.
   * Otherwise returns false and calls {@link BaseMvpViewStateDelegateCallback#onNewViewStateInstance()}
   */
  public boolean createOrRestoreViewState(ViewStateSavedState savedState) {

    if (createOrRestoreCalled) {
      return false;
    }
    createOrRestoreCalled = true;

    MvpViewStateViewGroupDelegateCallback delegate = (MvpViewStateViewGroupDelegateCallback) delegateCallback;

    // ViewState already exists
    if (delegate.getViewState() != null) {
      applyViewState = true;
      return false;
    }

    if (savedState != null) {
      ViewState viewState = savedState.getMosbyViewState();
      delegate.setViewState(viewState);
      boolean restoredFromBundle = delegate.getViewState() != null;

      if (restoredFromBundle) {
        applyViewState = true;
        return true;
      }
    }

    // Create view state
    delegate.setViewState(delegate.createViewState());
    if (delegate.getViewState() == null) {
      throw new NullPointerException(
          "ViewState is null! Do you return null in createViewState() method?");
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

    MvpViewStateViewGroupDelegateCallback delegate = (MvpViewStateViewGroupDelegateCallback) delegateCallback;

    if (applyViewState) {
      delegate.setRestoringViewState(true);
      delegate.getViewState().apply(delegate.getMvpView(), delegate.isRetainInstance());
      delegate.setRestoringViewState(false);
      delegate.onViewStateInstanceRestored(delegate.isRetainInstance());
      return true;
    }

    delegate.onNewViewStateInstance();
    return false;
  }

  /**
   * Saves {@link RestorableViewState} in a bundle. <b>Should be called from View
   * onSaveInstanceState(Parcelable) method</b>
   */
  public Parcelable saveViewState(Parcelable superState) {

    MvpViewStateViewGroupDelegateCallback delegate = (MvpViewStateViewGroupDelegateCallback) delegateCallback;

    boolean retainingInstanceState = delegate.isRetainInstance();

    ViewState viewState = delegate.getViewState();
    if (viewState == null) {
      throw new NullPointerException("ViewState is null! That's not allowed");
    }

    if (retainingInstanceState) {
      // For next time we call applyViewState() we have to set this flag to true
      applyViewState = true;
      return null;
    } else {
      ViewStateSavedState state = new ViewStateSavedState(superState);
      state.setMosbyViewState((RestorableParcelableViewState) delegate.getViewState());
      return state;
    }
  }
}
