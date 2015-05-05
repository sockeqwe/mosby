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
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * This is a enhancement of {@link com.hannesdorfmann.mosby.mvp.MvpActivity} that introduces the
 * support of {@link RestoreableViewState}.
 * <p>
 * You can change the behaviour of what to do if the viewstate is empty (usually if the activity
 * creates the viewState for the very first time and therefore has no state / data to restore) by
 * overriding {@link #onNewViewStateInstance()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpViewStateActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P>
    implements ViewStateSupport {


  protected ViewStateManager<?> viewStateManager = new ViewStateManager<>(this, this);
  protected RestoreableViewState viewState;

  /**
   * A simple flag that indicates if the restoring ViewState  is in progress right now.
   */
  protected boolean restoringViewState = false;


  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    createOrRestoreViewState(savedInstanceState);
    applyViewState();
  }

  /**
   * Creates or restores the viewstate
   *
   * @param savedInstanceState The Bundle that may or may not contain the viewstate
   * @return true if restored successfully, otherwise fals
   */
  protected boolean createOrRestoreViewState(Bundle savedInstanceState) {
    return viewStateManager.createOrRestoreViewState(savedInstanceState);
  }

  /**
   * Applies the view state. Checks internally if this is necessary and return true, if applied or
   * false if not applied (i.e. first time activity / fragment runs)
   */
  protected boolean applyViewState() {
    return viewStateManager.applyViewState();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    saveViewStateInstanceState(outState);
  }

  /**
   * Called from {@link #onSaveInstanceState(Bundle)} to store the bundle persistent
   *
   * @param outState The bundle to store
   */
  protected void saveViewStateInstanceState(Bundle outState) {
    viewStateManager.saveViewState(outState, false);
  }

  @Override public RestoreableViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    if (!(viewState instanceof RestoreableViewState)) {
      throw new IllegalArgumentException(
          "Only " + RestoreableViewState.class.getSimpleName() + " are allowed");
    }

    this.viewState = (RestoreableViewState) viewState;
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.restoringViewState = restoringViewState;
  }

  @Override public boolean isRestoringViewState() {
    return restoringViewState;
  }

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // not needed. You could override this is subclasses if needed
  }

  /**
   * Creates the ViewState instance
   */
  public abstract RestoreableViewState createViewState();
}
