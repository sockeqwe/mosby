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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;

/**
 * The {@link FragmentMvpDelegateImpl} with {@link ViewState} support
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class FragmentMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
    extends FragmentMvpDelegateImpl<V, P> {

  public static final boolean DEBUG = false;
  private static final String DEBUG_TAG = "FragmentMvpDelegateImpl";
  private MvpViewStateDelegateCallback<V, P, VS> delegateCallback;
  private boolean applyViewState = false;
  private boolean applyViewStateFromMemory = false;

  public FragmentMvpViewStateDelegateImpl(Fragment fragment,
      MvpViewStateDelegateCallback<V, P, VS> delegateCallback,
      boolean keepPresenterAndViewStateDuringScreenOrientationChange,
      boolean keepPresenterAndViewStateOnBackstack) {
    super(fragment, delegateCallback, keepPresenterAndViewStateDuringScreenOrientationChange,
        keepPresenterAndViewStateOnBackstack);
    this.delegateCallback = delegateCallback;
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    if (bundle != null && keepPresenterInstanceDuringScreenOrientationChanges) {

      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);

      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "MosbyView ID = " + mosbyViewId + " for MvpView: " + delegateCallback.getMvpView());
      }
    }

    if (mosbyViewId != null) {
      VS viewState = PresenterManager.getViewState(fragment.getActivity(), mosbyViewId);
      if (viewState != null) {
        //
        // ViewState restored from PresenterManager
        //
        delegateCallback.setViewState(viewState);
        applyViewState = true;
        applyViewStateFromMemory = true;
        if (DEBUG) {
          Log.d(DEBUG_TAG, "ViewState reused from Mosby internal cache for view: "
              + delegateCallback.getMvpView()
              + " viewState: "
              + viewState);
        }

        return;
      }
    }

    VS viewState = delegateCallback.createViewState();
    if (viewState == null) {
      throw new NullPointerException(
          "ViewState returned from createViewState() is null! MvpView that has returned null as ViewState is: "
              + delegateCallback.getMvpView());
    }

    if (bundle != null && viewState instanceof RestorableParcelableViewState) {
      // A little bit hacky that we need an instance of the viewstate to restore a view state
      // (may creates another view state object) but I don't know any better way :)
      RestorableViewState restoredViewState =
          ((RestorableParcelableViewState) viewState).restoreInstanceState(bundle);

      if (restoredViewState != null) {
        //
        // ViewState restored from bundle
        //
        viewState = (VS) restoredViewState;
        delegateCallback.setViewState(viewState);
        applyViewState = true;
        applyViewStateFromMemory = false;

        if (keepPresenterInstanceDuringScreenOrientationChanges) {
          if (mosbyViewId == null) {
            throw new IllegalStateException(
                "The (internal) Mosby View id is null although bundle is not null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
          }
          PresenterManager.putViewState(fragment.getActivity(), mosbyViewId, viewState);
        }

        if (DEBUG) {
          Log.d(DEBUG_TAG, "Recreated ViewState from bundle for view: "
              + delegateCallback.getMvpView()
              + " viewState: "
              + viewState);
        }

        return;
      }
    }

    //
    // Entirely new ViewState has been created, typically because the app is starting the first time
    //
    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      if (mosbyViewId == null) {
        throw new IllegalStateException(
            "The (internal) Mosby View id is null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
      }
      PresenterManager.putViewState(fragment.getActivity(), mosbyViewId, viewState);
    }
    delegateCallback.setViewState(viewState);
    applyViewState = false;
    applyViewStateFromMemory = false;

    if (DEBUG) {
      Log.d(DEBUG_TAG, "Created a new ViewState instance for view: "
          + delegateCallback.getMvpView()
          + " viewState: "
          + viewState);
    }
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (applyViewState) {
      VS viewState = delegateCallback.getViewState();
      V mvpView = delegateCallback.getMvpView();
      if (viewState == null) {
        throw new NullPointerException(
            "ViewState returned from getViewState() is null! MvpView " + mvpView);
      }

      delegateCallback.setRestoringViewState(true);
      viewState.apply(mvpView, applyViewStateFromMemory);
      delegateCallback.setRestoringViewState(false);
      delegateCallback.onViewStateInstanceRestored(applyViewStateFromMemory);
    } else {
      delegateCallback.onNewViewStateInstance();
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    boolean keepInstance = retainPresenterInstance();
    VS viewState = delegateCallback.getViewState();
    if (viewState == null) {
      throw new NullPointerException(
          "ViewState returned from getViewState() is null! The MvpView that has returned null in getViewState() is "
              + delegateCallback.getMvpView());
    }

    if (keepInstance && viewState instanceof RestorableViewState) {
      ((RestorableViewState) viewState).saveInstanceState(outState);
    }
  }
}
