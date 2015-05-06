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
package com.hannesdorfmann.mosby.mvp.viewstate.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.layout.MvpLinearLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateSupport;

/**
 * A {@link MvpLinearLayout} with viewstate support
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
public abstract class MvpViewStateLinearLayout<V extends MvpView, P extends MvpPresenter<V>> extends MvpLinearLayout<V, P>
    implements ViewStateSupport {

  private LayoutViewStateManager viewStateManager = new LayoutViewStateManager(this, this);
  private boolean restoringViewState = false;
  protected RestoreableParcelableViewState viewState;

  public MvpViewStateLinearLayout(Context context) {
    super(context);
  }

  public MvpViewStateLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MvpViewStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MvpViewStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    viewStateManager.createOrRestoreViewState(null);
    applyViewState();
  }

  @Override protected Parcelable onSaveInstanceState() {
    Parcelable superParcelable = super.onSaveInstanceState();

    Parcelable vsParcelable = saveViewState(superParcelable);
    if (vsParcelable != null) {
      return vsParcelable;
    } else {
      return superParcelable;
    }
  }

  protected Parcelable saveViewState(Parcelable state) {
    return viewStateManager.saveViewState(state, false);
  }

  @Override protected void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof ViewStateSavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    ViewStateSavedState vsState = (ViewStateSavedState) state;
    restoreViewState(vsState);
    super.onRestoreInstanceState(vsState.getSuperState());
  }

  protected boolean restoreViewState(ViewStateSavedState state) {
    return viewStateManager.createOrRestoreViewState(state);
  }

  protected void applyViewState() {
    viewStateManager.applyViewState();
  }

  @Override public RestoreableParcelableViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    this.viewState = (RestoreableParcelableViewState) viewState;
  }

  @Override public void setRestoringViewState(boolean retstoringViewState) {
    this.restoringViewState = retstoringViewState;
  }

  @Override public boolean isRestoringViewState() {
    return restoringViewState;
  }

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // can be overridden in subclass
  }
}
