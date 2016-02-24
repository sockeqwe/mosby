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
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * A {@link ViewGroupMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ViewGroupMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    extends ViewGroupMvpDelegateImpl<V, P> {

  private boolean applyViewState = false;
  private boolean viewStateRetained = false;

  public ViewGroupMvpViewStateDelegateImpl(
      ViewGroupViewStateDelegateCallback<V, P> delegateCallback) {
    super(delegateCallback);
  }

  @Override protected MosbySavedState createSavedState(Parcelable superState) {

    ViewGroupViewStateDelegateCallback<V, P> castedDelegate =
        (ViewGroupViewStateDelegateCallback<V, P>) delegateCallback;

    MosbyViewStateSavedState state = new MosbyViewStateSavedState(superState);
    state.setMosbyViewId(viewId);
    ViewState<V> viewState = castedDelegate.getViewState();

    if (viewState instanceof RestorableParcelableViewState) {
      state.setMosbyViewState((RestorableParcelableViewState<V>) viewState);
    }

    return state;
  }

  @Override protected void restoreSavedState(MosbySavedState state) {
    super.restoreSavedState(state);
    MosbyViewStateSavedState mosbySavedState = (MosbyViewStateSavedState) state;

    ViewGroupViewStateDelegateCallback<V, P> castedDelegate =
        (ViewGroupViewStateDelegateCallback<V, P>) delegateCallback;

    ViewState<V> memoryViewState =
        orientationChangeManager.getViewState(viewId, delegateCallback.getContext());

    if (memoryViewState != null) {
      // viewstate from memory
      castedDelegate.setViewState(memoryViewState);
      applyViewState = true;
      viewStateRetained = true;
    } else if (mosbySavedState.getMosbyViewState() != null) {
      castedDelegate.setViewState(mosbySavedState.getMosbyViewState());
      applyViewState = true;
      viewStateRetained = false;
    } else {
      // No viewState found
      applyViewState = false;
    }
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (applyViewState) {
      ((ViewGroupViewStateDelegateCallback) delegateCallback).getViewState()
          .apply(delegateCallback.getMvpView(), viewStateRetained);
    }
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Context context = delegateCallback.getContext();

    if (delegateCallback.isRetainInstance()
        && orientationChangeManager.willViewBeDetachedBecauseOrientationChange(context)) {
      orientationChangeManager.putViewState(viewId,
          ((ViewGroupViewStateDelegateCallback) delegateCallback).getViewState(), context);
    }
  }
}
