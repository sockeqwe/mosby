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

import android.content.Context;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * An enhanced version of {@link MvpDelegateCallback} that adds support
 * for
 * android.view.View like FrameLayout etc.
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
public interface ViewGroupDelegateCallback<V extends MvpView, P extends MvpPresenter<V>>
    extends MvpDelegateCallback<V, P> {

  /**
   * This method must call super.onSaveInstanceState() within any view
   */
  public Parcelable superOnSaveInstanceState();

  /**
   * This methdo must call super.onRestoreInstanceState(state)
   *
   * @param state The parcelable containing the state
   */
  public void superOnRestoreInstanceState(Parcelable state);

  /**
   * Get the context
   *
   * @return Get the context
   * @since 3.0
   */
  public Context getContext();
}
