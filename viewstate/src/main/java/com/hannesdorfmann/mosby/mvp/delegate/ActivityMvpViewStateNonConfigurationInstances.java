/*
 *  Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.mvp.delegate;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * This kind of class is used in Activities to save the Presenter and ViewState in retaining
 * activities. It's a mosby internal class.
 *
 * @author Hannes Dorfmann
 * @since 2.0.0
 */
class ActivityMvpViewStateNonConfigurationInstances<V extends MvpView, P extends MvpPresenter<V>>
    extends ActivityMvpNonConfigurationInstances {

  /**
   * The retaining ViewState
   */
  ViewState<V> viewState;

  public ActivityMvpViewStateNonConfigurationInstances(MvpPresenter presenter,
      ViewState<V> viewState, Object nonMosbyCustomConfigurationInstance) {
    super(presenter, nonMosbyCustomConfigurationInstance);
    this.viewState = viewState;
  }
}
