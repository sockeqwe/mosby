/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3;

import android.content.Context;
import android.os.Parcelable;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * An enhanced version of {@link MviDelegateCallback} that adds support
 * for
 * android.view.ViewGroup like FrameLayout etc.
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
public interface ViewGroupMviDelegateCallback<V extends MvpView, P extends MviPresenter<V, ?>>
    extends MviDelegateCallback<V, P> {

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
