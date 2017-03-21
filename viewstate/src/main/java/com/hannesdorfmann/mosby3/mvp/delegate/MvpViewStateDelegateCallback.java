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

package com.hannesdorfmann.mosby3.mvp.delegate;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;

/**
 * An enhanced version of {@link MvpDelegateCallback} that adds {@link ViewState} support.
 * This interface must be implemented by all (subclasses of) Activity and Fragment
 * that want to support {@link
 * ViewState} and mvp.
 *
 * @author Hannes Dorfmann
 * @see ViewGroupMvpViewStateDelegateCallback
 * @since 1.0.0
 */
public interface MvpViewStateDelegateCallback<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
    extends MvpDelegateCallback<V, P> {

  /**
   * Get the viewState
   */
  VS getViewState();

  /**
   * Set the viewstate. <b>Should only be called by {@link MvpViewStateInternalDelegate}</b>
   */
  void setViewState(VS viewState);

  /**
   * Create the viewstate.
   */
  @NonNull VS createViewState();

  /**
   * This method will be called by {@link MvpViewStateInternalDelegate} to inform that restoring
   * the
   * view state
   * is in progress.
   *
   * @param restoringViewState true, if restoring viewstate is in progress, otherwise false
   */
  void setRestoringViewState(boolean restoringViewState);

  /**
   * @return true if the viewstate is restoring right now (not finished yet). Otherwise false.
   */
  boolean isRestoringViewState();

  /**
   * Called if the {@link ViewState} instance has been restored successfully.
   * <p>
   * In this method you have to restore the viewstate by reading the view state properties and
   * setup
   * the view to be on the same state as before.
   * </p>
   *
   * @param instanceStateRetained true, if the viewstate has been retained by using{@link
   * Fragment#setRetainInstance(boolean)}, otherwise false (always false for activities).
   */
  void onViewStateInstanceRestored(boolean instanceStateRetained);

  /**
   * Called if a new {@link ViewState} has been created because no viewstate from a previous
   * Activity or Fragment instance could be restored.
   * <p><b>Typically this is called on the first time the <i>Activity</i> or <i>Fragment</i> starts
   * and therefore no view state instance previously exists</b></p>
   */
  void onNewViewStateInstance();
}
