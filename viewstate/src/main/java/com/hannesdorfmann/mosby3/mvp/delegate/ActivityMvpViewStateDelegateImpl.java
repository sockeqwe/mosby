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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The default implementation for {@link ActivityMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ActivityMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
    extends ActivityMvpDelegateImpl<V, P> {

  @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL", justification = "Could be enabled for debugging purpose")
  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "ActivityMvpViewStateDel";

  private MvpViewStateDelegateCallback<V, P, VS> delegateCallback;

  /**
   * @param activity The activity
   * @param delegateCallback The callback
   * @param keepPresenterAndViewState true, if the presenter and the view state should be kept
   * across screen orientation changes (and if ViewState is instance of {@link RestorableViewState}
   * it will be saved persistently in a bundle to survive process death). Otherwise, false
   */
  public ActivityMvpViewStateDelegateImpl(Activity activity,
      MvpViewStateDelegateCallback<V, P, VS> delegateCallback, boolean keepPresenterAndViewState) {
    super(activity, delegateCallback, keepPresenterAndViewState);
    this.delegateCallback = delegateCallback;
  }

  private void setViewState(@NonNull VS viewState, boolean applyViewState,
      boolean applyViewStateFromMemory) {

    if (viewState == null) {
      throw new IllegalStateException(
          "Oops, viewState is null! This seems to be a Mosby internal bug. Please report this issue at https://github.com/sockeqwe/mosby/issues");
    }

    delegateCallback.setViewState(viewState);

    if (applyViewState) {
      delegateCallback.setRestoringViewState(true);
      delegateCallback.getViewState()
          .apply(delegateCallback.getMvpView(), applyViewStateFromMemory);
      delegateCallback.setRestoringViewState(false);
      delegateCallback.onViewStateInstanceRestored(applyViewStateFromMemory);
    }
  }

  @Override public void onPostCreate(Bundle bundle) {
    super.onPostCreate(bundle);

    if (mosbyViewId != null) {
      VS viewState = PresenterManager.getViewState(activity, mosbyViewId);
      if (viewState != null) {
        //
        // ViewState restored from PresenterManager
        //
        setViewState(viewState, true, true);
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

    if (bundle != null && viewState instanceof RestorableViewState) {
      // A little bit hacky that we need an instance of the viewstate to restore a view state
      // (may creates another view state object) but I don't know any better way :)
      RestorableViewState restoredViewState =
          ((RestorableViewState) viewState).restoreInstanceState(bundle);

      if (restoredViewState != null) {
        //
        // ViewState restored from bundle
        //
        viewState = (VS) restoredViewState;
        setViewState(viewState, true, false);
        if (keepPresenterInstance) {
          if (mosbyViewId == null) {
            throw new IllegalStateException(
                "The (internal) Mosby View id is null although bundle is not null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
          }
          PresenterManager.putViewState(activity, mosbyViewId, viewState);
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
    if (keepPresenterInstance) {
      if (mosbyViewId == null) {
        throw new IllegalStateException(
            "The (internal) Mosby View id is null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
      }
      PresenterManager.putViewState(activity, mosbyViewId, viewState);
    }
    setViewState(viewState, false, false);

    if (DEBUG) {
      Log.d(DEBUG_TAG, "Created a new ViewState instance for view: "
          + delegateCallback.getMvpView()
          + " viewState: "
          + viewState);
    }

    delegateCallback.onNewViewStateInstance();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    boolean keepInstance = retainPresenterInstance(keepPresenterInstance, activity);
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
