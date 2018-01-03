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
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * The MvpDelegate callback that will be called from {@link
 * FragmentMvpDelegate} or {@link ViewGroupMvpDelegate}. This interface must be implemented by all
 * Fragment or android.view.View that you want to support Mosby mvp.
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public interface MvpDelegateCallback<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * Creates the presenter instance
   *
   * @return the created presenter instance
   */
  @NonNull P createPresenter();

  /**
   * Gets the presenter. If null is returned, then a internally a new presenter instance gets
   * created by calling {@link #createPresenter()}
   *
   * @return the presenter instance. can be null.
   */
  P getPresenter();

  /**
   * Sets the presenter instance
   *
   * @param presenter The presenter instance
   */
  void setPresenter(P presenter);

  /**
   * Gets the MvpView for the presenter
   *
   * @return The view associated with the presenter
   */
  V getMvpView();
}

