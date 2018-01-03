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

package com.hannesdorfmann.mosby3.mvp;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * The base interface for each mvp presenter.
 *
 * <p>
 * Mosby assumes that all interaction (i.e. updating the View) between Presenter and View is
 * executed on Android's main UI thread.
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface MvpPresenter<V extends MvpView> {

  /**
   * Set or attach the view to this presenter
   */
  @UiThread
  void attachView(@NonNull V view);

  /**
   * Will be called if the view has been destroyed. Typically this method will be invoked from
   * <code>Activity.detachView()</code> or <code>Fragment.onDestroyView()</code>
   *
   * @deprecated This method has been split into 2 methods: {@link #detachView()} and {@link #destroy()}
    */
  @UiThread
  @Deprecated
  void detachView(boolean retainInstance);

  /**
   * Will be called if the view has been detached from the Presenter.
   * Usually this happens on screen orientation changes or view (like fragment) has been put on the backstack.
   */
  @UiThread
  void detachView();

  /**
   * Will be called if the presenter is no longer needed because the View has been destroyed permanently.
   * This is where you do clean up stuff.
   */
  @UiThread
  void destroy();
}
