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
package com.hannesdorfmann.mosby3.mvi.layout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateCallback;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegate;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateImpl;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * A FrameLayout that can be used as View with an Presenter to implement MVI
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public abstract class MviFrameLayout<V extends MvpView, P extends MviPresenter<V, ?>>
    extends FrameLayout implements ViewGroupMviDelegateCallback<V, P>, MvpView {


  private boolean isRestoringViewState = false;

  protected ViewGroupMviDelegate<V, P> mvpDelegate;

  public MviFrameLayout(Context context) {
    super(context);
  }

  public MviFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MviFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MviFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }


  /**
   * Get the mvp delegate. This is internally used for creating presenter, attaching and detaching
   * view from presenter etc.
   *
   * <p><b>Please note that only one instance of mvp delegate should be used per android.view.View
   * instance</b>.
   * </p>
   *
   * <p>
   * Only override this method if you really know what you are doing.
   * </p>
   *
   * @return {@link ViewGroupMviDelegate}
   */
  @NonNull protected ViewGroupMviDelegate<V, P> getMviDelegate() {
    if (mvpDelegate == null) {
      mvpDelegate = new ViewGroupMviDelegateImpl<>(this, this, true);
    }

    return mvpDelegate;
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    getMviDelegate().onAttachedToWindow();
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    getMviDelegate().onDetachedFromWindow();
  }

  @SuppressLint("MissingSuperCall") @Override public Parcelable onSaveInstanceState() {
    return getMviDelegate().onSaveInstanceState();
  }

  @SuppressLint("MissingSuperCall") @Override
  public void onRestoreInstanceState(Parcelable state) {
    getMviDelegate().onRestoreInstanceState(state);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this view
   */
  public abstract P createPresenter();

  @Override public V getMvpView() {
    return (V) this;
  }



  @Override public final Parcelable superOnSaveInstanceState() {
    return super.onSaveInstanceState();
  }

  @Override public final void superOnRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);
  }


  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.isRestoringViewState = restoringViewState;
  }

  protected boolean isRestoringViewState() {
    return isRestoringViewState;
  }
}
