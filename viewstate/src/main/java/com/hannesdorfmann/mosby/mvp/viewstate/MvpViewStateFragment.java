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
import android.support.annotation.Nullable;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * This is a enhancement of {@link com.hannesdorfmann.mosby.mvp.MvpFragment} that introduces the
 * support of {@link com.hannesdorfmann.mosby.mvp.viewstate.ViewState}.
 * <p>
 * You can change the behaviour of what to do if the viewstate is empty (usually if the fragment
 * creates the viewState for the very first time and therefore has no state / data to restore) by
 * overriding {@link #onNewViewStateInstance()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpViewStateFragment<V extends MvpView, P extends MvpPresenter<V>> extends MvpFragment<V, P>
    implements ViewStateSupport {

  protected ViewStateManager<?> viewStateManager = new ViewStateManager<>(this, this);

  /**
   * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
   * #onViewCreated(View, Bundle)}. Don't instantiate it by hand.
   */
  protected ViewState viewState;

  /**
   * A simple flag that indicates if the restoring ViewState  is in progress right now.
   */
  private boolean restoringViewState = false;

  /**
   * Create the view state object of this class
   */
  public abstract ViewState createViewState();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Will be called at first time fragment
    // get's instantiated for both retaining and not retaining fragments
    createOrRestoreViewState(savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  /**
   * Creates or restores the viewstate instance. It doesn't apply the viewstate to this view. that
   * will be done in {@link #applyViewState()}
   *
   * @param savedInstanceState The Bundle that may or may not contain the viewstate
   * @return true if restored successfully, otherwise fals
   */
  protected boolean createOrRestoreViewState(Bundle savedInstanceState) {
    return viewStateManager.createOrRestoreViewState(savedInstanceState);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    applyViewState();
  }

  /**
   * Applys the view state. That means, that the view state instance calls the methods of the view
   * to get back to the view state before screen orientation change happened.
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
    viewStateManager.saveViewState(outState, getRetainInstance());
  }

  @Override public ViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    this.viewState = viewState;
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
}
