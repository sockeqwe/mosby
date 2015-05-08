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
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.ViewStateSavedState;

/**
 * A {@link ViewGroupMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ViewGroupMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    extends ViewGroupMvpDelegateImpl<V, P> {

  public ViewGroupMvpViewStateDelegateImpl(MvpViewStateViewGroupDelegateCallback<V, P> delegateCallback) {
    super(delegateCallback);
  }

  @Override protected MvpInternalDelegate<V, P> getInternalDelegate() {
    if (internalDelegate == null) {
      internalDelegate = new MvpInternalLayoutViewStateDelegate<V, P>(
          (MvpViewStateViewGroupDelegateCallback) delegateCallback);
    }

    return internalDelegate;
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    MvpInternalLayoutViewStateDelegate internal =
        (MvpInternalLayoutViewStateDelegate) getInternalDelegate();
    internal.createOrRestoreViewState(null);
    internal.applyViewState();
  }

  /**
   * Must be called from {@link View#onSaveInstanceState()}
   */
  public Parcelable onSaveInstanceState() {

    MvpViewStateViewGroupDelegateCallback delegate = (MvpViewStateViewGroupDelegateCallback) delegateCallback;
    Parcelable superParcelable = delegate.superOnSaveInstanceState();

    Parcelable vsParcelable =
        ((MvpInternalLayoutViewStateDelegate) getInternalDelegate()).saveViewState(superParcelable);
    if (vsParcelable != null) {
      return vsParcelable;
    } else {
      return superParcelable;
    }
  }

  /**
   * Must be called from {@link View#onRestoreInstanceState(Parcelable)}
   */
  public void onRestoreInstanceState(Parcelable state) {

    MvpViewStateViewGroupDelegateCallback delegate = (MvpViewStateViewGroupDelegateCallback) delegateCallback;

    if (!(state instanceof ViewStateSavedState)) {
      delegate.superOnRestoreInstanceState(state);
      return;
    }

    ViewStateSavedState vsState = (ViewStateSavedState) state;
    ((MvpInternalLayoutViewStateDelegate) getInternalDelegate()).createOrRestoreViewState(vsState);

    delegate.superOnRestoreInstanceState(vsState.getSuperState());
  }
}
