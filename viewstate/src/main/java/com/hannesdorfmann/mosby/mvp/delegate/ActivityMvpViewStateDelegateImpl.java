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

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * The default implementation for {@link ActivityMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ActivityMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    extends ActivityMvpDelegateImpl<V, P> {

  public ActivityMvpViewStateDelegateImpl(
      ActivityMvpViewStateDelegateCallback<V, P> delegateCallback) {
    super(delegateCallback);
  }

  @Override protected MvpInternalDelegate<V, P> getInternalDelegate() {
    if (internalDelegate == null) {
      internalDelegate =
          new MvpViewStateInternalDelegate<>((BaseMvpViewStateDelegateCallback) delegateCallback);
    }

    return internalDelegate;
  }

  @Override public void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    ActivityMvpViewStateDelegateCallback<V, P> viewStateDelegateCallback =
        (ActivityMvpViewStateDelegateCallback<V, P>) delegateCallback;

    ActivityMvpViewStateNonConfigurationInstances<V, P> nci =
        (ActivityMvpViewStateNonConfigurationInstances<V, P>) delegateCallback.getLastCustomNonConfigurationInstance();

    // Set the retaining view state
    if (nci != null && nci.viewState != null) {
      viewStateDelegateCallback.setViewState(nci.viewState);
    }

    MvpViewStateInternalDelegate internal = (MvpViewStateInternalDelegate) getInternalDelegate();
    internal.createOrRestoreViewState(savedInstanceState);
    internal.applyViewState();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).saveViewState(outState);
  }

  @Override public Object onRetainCustomNonConfigurationInstance() {

    ActivityMvpViewStateDelegateCallback<V, P> viewStateDelegateCallback =
        (ActivityMvpViewStateDelegateCallback<V, P>) delegateCallback;

    P presenter =
        viewStateDelegateCallback.shouldInstanceBeRetained() ? viewStateDelegateCallback.getPresenter()
            : null;
    ViewState<V> viewState =
        viewStateDelegateCallback.shouldInstanceBeRetained() ? viewStateDelegateCallback.getViewState()
            : null;

    Object nonMosbyConfiguraionInstance =
        delegateCallback.onRetainNonMosbyCustomNonConfigurationInstance();

    if (presenter == null && nonMosbyConfiguraionInstance == null && viewState == null) {
      return null;
    }

    return new ActivityMvpViewStateNonConfigurationInstances<>(presenter, viewState,
        nonMosbyConfiguraionInstance);
  }
}
