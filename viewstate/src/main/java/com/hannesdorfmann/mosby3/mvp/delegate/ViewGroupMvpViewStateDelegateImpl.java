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

import android.content.Context;
import android.os.Parcelable;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;

/**
 * A {@link ViewGroupMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ViewGroupMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    extends ViewGroupMvpDelegateImpl<V, P> {

  private ViewState<V> restoredParcelableViewState = null;

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
    restoredParcelableViewState = mosbySavedState.getMosbyViewState();
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    ViewGroupViewStateDelegateCallback<V, P> castedCallback =
        ((ViewGroupViewStateDelegateCallback<V, P>) delegateCallback);

    ViewState<V> memoryViewState =
        orientationChangeManager.getViewState(viewId, delegateCallback.getContext());

    if (memoryViewState != null) {
      // ViewState in memory
      castedCallback.setRestoringViewState(true);
      castedCallback.setViewState(memoryViewState);
      memoryViewState.apply(castedCallback.getMvpView(), true);
      castedCallback.setRestoringViewState(false);
      restoredParcelableViewState = null; // free memory
      castedCallback.onViewStateInstanceRestored(true);
    } else if (restoredParcelableViewState != null) {
      // ViewState from parcelable
      castedCallback.setRestoringViewState(true);
      castedCallback.setViewState(restoredParcelableViewState);
      restoredParcelableViewState.apply(castedCallback.getMvpView(), false);
      castedCallback.setRestoringViewState(false);
      restoredParcelableViewState = null; // free memory
      castedCallback.onViewStateInstanceRestored(false);
    } else {
      // No view state, launching for first time
      ViewState<V> viewState = castedCallback.createViewState();
      if (viewState == null) {
        throw new NullPointerException("ViewState returned from createViewState() is null! View is "
            + castedCallback.getMvpView());
      }
      castedCallback.setViewState(viewState);
      castedCallback.onNewViewStateInstance();
    }
  }

  @Override public void onDetachedFromWindow() {
    Context context = delegateCallback.getContext();

    if (delegateCallback.isRetainInstance()
        && !orientationChangeManager.willViewBeDestroyedPermanently(context)) {
      orientationChangeManager.putViewState(viewId,
          ((ViewGroupViewStateDelegateCallback) delegateCallback).getViewState(), context);
    }

    // super will do the cleanup
    super.onDetachedFromWindow();
  }
}
