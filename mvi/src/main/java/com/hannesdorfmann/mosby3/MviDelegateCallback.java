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

package com.hannesdorfmann.mosby3;

import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * This callback that will be called to instantiate the presenter and to get the view.
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public interface MviDelegateCallback<V extends MvpView, P extends MviPresenter<V, ?>> {

  /**
   * Creates the presenter instance
   *
   * @return the created presenter instance
   */
  @NonNull public P createPresenter();

  /**
   * Get the MvpView for the presenter
   *
   * @return The view associated with the presenter
   */
  @NonNull public V getMvpView();
}
