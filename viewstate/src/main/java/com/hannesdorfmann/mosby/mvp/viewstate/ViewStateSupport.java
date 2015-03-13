/*
 * Copyright (c) 2015 Hannes Dorfmann.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.mvp.viewstate;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * I know, that word doesn't exist in english :)
 * <p>
 * I was looking for a word to say: The class that implements this interface has a {@link
 * ViewState}. It's just a common interface  for Activities and Fragments that support {@link
 * ViewState} that is used with the  {@link ViewStateManager} to rededuce code clones (copy and
 * pasting through subclasses)
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface ViewStateSupport<V extends MvpView> {

  /**
   * Get the viewState
   */
  ViewState<V> getViewState();

  /**
   * Set the viewstate. <b>Should only be called by {@link ViewStateManager}</b>
   */
  void setViewState(ViewState<V> viewState);

  /**
   * Create the viewstate. Will be called by the {@link ViewStateManager}.
   */
  ViewState<V> createViewState();

  /**
   * Specify here what to do if the viewstate is empty (the activity / fragment
   * creates the viewState for the very first time and therefore has no state / data to restore) by
   * overriding {@link #onEmptyViewState()}
   */
  void onEmptyViewState();
}
