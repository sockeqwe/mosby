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

package com.hannesdorfmann.mosby3.mvp.delegate;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * This kind of class is used in Activities to save the presenter in retaining activities.  It's a
 * mosby internal class.
 *
 * @author Hannes Dorfmann
 * @since 2.0.0
 */
class ActivityMvpNonConfigurationInstances<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * The reference to the presenter
   */
  P presenter;

  /**
   * The reference to the custom non configuration.
   */
  Object nonMosbyCustomConfigurationInstance;

  /**
   * Constructor
   *
   * @param presenter The retaining presenter
   * @param nonMosbyCustomConfigurationInstance the other custom object
   */
  ActivityMvpNonConfigurationInstances(P presenter, Object nonMosbyCustomConfigurationInstance) {
    this.presenter = presenter;
    this.nonMosbyCustomConfigurationInstance = nonMosbyCustomConfigurationInstance;
  }
}
