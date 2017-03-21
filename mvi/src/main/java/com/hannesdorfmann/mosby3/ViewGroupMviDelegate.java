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

import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * The mvp delegate used for everything that derives from {@link View} like {@link FrameLayout}
 * etc.
 *
 * <p>
 * The following methods must be called from the corresponding View lifecycle method:
 * <ul>
 * <li>{@link #onAttachedToWindow()}</li>
 * <li>{@link #onDetachedFromWindow()}</li>
 * <li>{@link #onSaveInstanceState()}</li>
 * <li>{@link #onRestoreInstanceState(Parcelable)} ()}</li>
 * </ul>
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public interface ViewGroupMviDelegate<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * Must be called from {@link View#onAttachedToWindow()}
   */
  void onAttachedToWindow();

  /**
   * Must be called from {@link View#onDetachedFromWindow()}
   */
  void onDetachedFromWindow();

  /**
   * Must be called from {@link View#onRestoreInstanceState(Parcelable)}
   *
   * @param state The parcelable state
   */
  void onRestoreInstanceState(Parcelable state);

  /**
   * Save the instatnce state
   *
   * @return The state with all the saved data
   */
  Parcelable onSaveInstanceState();
}
