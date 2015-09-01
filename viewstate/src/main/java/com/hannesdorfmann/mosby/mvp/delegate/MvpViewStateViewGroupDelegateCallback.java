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

import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * An enhanced version of {@link BaseMvpViewStateDelegateCallback} that adds {@link ViewState} support for
 * android.view.View like FrameLayout etc.
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public interface MvpViewStateViewGroupDelegateCallback<V extends MvpView, P extends MvpPresenter<V>>
    extends BaseMvpViewStateDelegateCallback<V, P> {

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
}
