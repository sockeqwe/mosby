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
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import java.util.UUID;

/**
 * A {@link ViewGroupMvpDelegate} that supports {@link ViewState}
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ViewGroupMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
    implements ViewGroupMvpDelegate<V, P> {

  // TODO allow custom save state hook in

  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "ViewGroupMvpDelegateImp";

  private ViewGroupMvpViewStateDelegateCallback<V, P, VS> delegateCallback;
  private String mosbyViewId;
  private final boolean keepPresenter;
  private final Activity activity;
  private final boolean isInEditMode;
  private VS restoreableParcelableViewState = null;

  private boolean applyViewState = false;
  private boolean viewStateFromMemoryRestored = false;

  /**
   * Creates a new instance
   *
   * @param delegateCallback the callback
   * @param keepPresenter true, if you want to keep the presenter and view state during screen
   * orientation changes etc.
   */
  public ViewGroupMvpViewStateDelegateImpl(@NonNull View view,
      @NonNull ViewGroupMvpViewStateDelegateCallback<V, P, VS> delegateCallback,
      boolean keepPresenter) {
    if (view == null) {
      throw new NullPointerException("View is null!");
    }
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }
    this.delegateCallback = delegateCallback;
    this.keepPresenter = keepPresenter;
    this.isInEditMode = view.isInEditMode();
    if (!isInEditMode) {
      this.activity = PresenterManager.getActivity(delegateCallback.getContext());
    } else {
      this.activity = null;
    }
  }

  /**
   * Generates the unique (mosby internal) viewState id and calls {@link
   * MvpDelegateCallback#createPresenter()}
   * to create a new presenter instance
   *
   * @return The new created presenter instance
   */
  private P createViewIdAndCreatePresenter() {

    P presenter = delegateCallback.createPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter returned from createPresenter() is null.");
    }
    if (keepPresenter) {
      Context context = delegateCallback.getContext();
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(PresenterManager.getActivity(context), mosbyViewId, presenter);
    }
    return presenter;
  }

  private VS createViewState() {
    VS viewState = delegateCallback.createViewState();
    if (keepPresenter) {
      PresenterManager.putViewState(activity, mosbyViewId, viewState);
    }
    applyViewState = false;
    viewStateFromMemoryRestored = false;
    return viewState;
  }

  @NonNull private Context getContext() {
    Context c = delegateCallback.getContext();
    if (c == null) {
      throw new NullPointerException("Context returned from " + delegateCallback + " is null");
    }
    return c;
  }

  @Override public void onAttachedToWindow() {
    if (isInEditMode) return;

    P presenter = null;
    VS viewState = null;

    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterInstance == false)
      presenter = createViewIdAndCreatePresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "new Presenter instance created: "
            + presenter
            + " MvpView: "
            + delegateCallback.getMvpView());
      }

      viewState = createViewState();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "new ViewState instance created: "
            + viewState
            + " MvpView: "
            + delegateCallback.getMvpView());
      }
    } else {
      presenter = PresenterManager.getPresenter(activity, mosbyViewId);
      if (presenter == null) {
        // Process death,
        // hence no presenter with the given viewState id stored, although we have a viewState id
        presenter = createViewIdAndCreatePresenter();
        if (DEBUG) {
          Log.d(DEBUG_TAG,
              "No Presenter instance found in cache, although MosbyView ID present. This was caused by process death, therefore new Presenter instance created: "
                  + presenter);
        }
      } else {
        if (DEBUG) {
          Log.d(DEBUG_TAG, "Presenter instance reused from internal cache: "
              + presenter
              + " MvpView: "
              + delegateCallback.getMvpView());
        }
      }

      viewState = PresenterManager.getViewState(activity, mosbyViewId);
      if (viewState == null) {

        if (restoreableParcelableViewState == null) {
          // Process death, no viewstate restored from parcel
          viewState = createViewState();
          if (DEBUG) {
            Log.d(DEBUG_TAG,
                "No ViewState instance found in cache, although MosbyView ID present. This was caused by process death, therefore new ViewState instance created: "
                    + viewState);
          }
        } else {
          // Memory ViewState is null, so use RestoreableViewState
          viewState = restoreableParcelableViewState;
          applyViewState = true;
          viewStateFromMemoryRestored = false;

          if (keepPresenter) {
            if (mosbyViewId == null) {
              throw new IllegalStateException(
                  "The (internal) Mosby View id is null although restoreable view state (Parcelable) is not null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
            }
            // Put restoreable view state into memory cache for future useage
            PresenterManager.putViewState(activity, mosbyViewId, viewState);
          }

          if (DEBUG) {
            Log.d(DEBUG_TAG, "Parcelable ViewState instance reused from last SavedState: "
                + viewState
                + " MvpView: "
                + delegateCallback.getMvpView());
          }
        }
      } else {
        // ViewState from memory
        applyViewState = true;
        viewStateFromMemoryRestored = true;
        if (DEBUG) {
          Log.d(DEBUG_TAG, "ViewState instance reused from internal cache: "
              + viewState
              + " MvpView: "
              + delegateCallback.getMvpView());
        }
      }
    }

    // presenter is ready, so attach viewState
    V view = delegateCallback.getMvpView();
    if (view == null) {
      throw new NullPointerException(
          "MvpView returned from getMvpView() is null. Returned by " + delegateCallback);
    }

    if (presenter == null) {
      throw new IllegalStateException(
          "Oops, Presenter is null. This seems to be a Mosby internal bug. Please report this issue here: https://github.com/sockeqwe/mosby/issues");
    }

    if (viewState == null) {
      throw new IllegalStateException(
          "Oops, ViewState is null. This seems to be a Mosby internal bug. Please report this issue here: https://github.com/sockeqwe/mosby/issues");
    }

    delegateCallback.setViewState(viewState);

    if (applyViewState) {
      delegateCallback.setRestoringViewState(true);
    }

    delegateCallback.setPresenter(presenter);
    presenter.attachView(view);
    if (DEBUG) {
      Log.d(DEBUG_TAG,
          "MvpView attached to Presenter. MvpView: " + view + "   Presenter: " + presenter);
    }

    if (applyViewState) {
      viewState.apply(view, viewStateFromMemoryRestored);
      delegateCallback.setRestoringViewState(false);
      delegateCallback.onViewStateInstanceRestored(viewStateFromMemoryRestored);
      if (DEBUG) {
        Log.d(DEBUG_TAG, "ViewState restored (from memory = "
            + viewStateFromMemoryRestored
            + " ). MvpView: "
            + view
            + "   ViewState: "
            + viewState);
      }
    } else {
      delegateCallback.onNewViewStateInstance();
    }
  }

  @Override public void onDetachedFromWindow() {
    if (isInEditMode) return;

    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      throw new NullPointerException(
          "Presenter returned from delegateCallback.getPresenter() is null");
    }

    if (keepPresenter) {

      boolean destroyedPermanently =
          !ActivityMvpDelegateImpl.retainPresenterInstance(keepPresenter, activity);

      if (destroyedPermanently) {
        // Whole activity will be destroyed
        if (DEBUG) {
          Log.d(DEBUG_TAG, "Detaching View "
              + delegateCallback.getMvpView()
              + " from Presenter "
              + presenter
              + " and removing presenter permanently from internal cache because the hosting Activity will be destroyed permanently");
        }

        if (mosbyViewId != null) { // mosbyViewId == null if keepPresenter == false
          PresenterManager.remove(activity, mosbyViewId);
        }
        mosbyViewId = null;
        presenter.detachView(false);
      } else {
        boolean detachedBecauseOrientationChange =
            ActivityMvpDelegateImpl.retainPresenterInstance(keepPresenter, activity);

        if (detachedBecauseOrientationChange) {
          // Simple orientation change
          if (DEBUG) {
            Log.d(DEBUG_TAG, "Detaching View "
                + delegateCallback.getMvpView()
                + " from Presenter "
                + presenter
                + " temporarily because of orientation change");
          }
          presenter.detachView(true);
        } else {
          // view detached, i.e. because of back stack / navigation
          /*
          if (DEBUG) {
            Log.d(DEBUG_TAG, "Detaching View "
                + delegateCallback.getMvpView()
                + " from Presenter "
                + presenter
                + " because view has been destroyed. Also Presenter is removed permanently from internal cache.");
          }
          PresenterManager.remove(activity, mosbyViewId);
          mosbyViewId = null;
          presenter.detachView(false);
          */
        }
      }
    } else {
      // retain instance feature disabled

      if (DEBUG) {
        Log.d(DEBUG_TAG, "Detaching View "
            + delegateCallback.getMvpView()
            + " from Presenter "
            + presenter
            + " permanently");
      }

      presenter.detachView(false);
      if (mosbyViewId != null) { // mosbyViewId == null if keepPresenter == false
        PresenterManager.remove(activity, mosbyViewId);
      }
      mosbyViewId = null;
    }
  }

  /**
   * Must be called from {@link View#onSaveInstanceState()}
   */
  @Override public Parcelable onSaveInstanceState() {
    if (isInEditMode) return null;

    VS viewState = delegateCallback.getViewState();
    if (viewState == null) {
      throw new NullPointerException("ViewState returned from getViewState() is null for MvpView "
          + delegateCallback.getMvpView());
    }

    Parcelable superState = delegateCallback.superOnSaveInstanceState();

    if (keepPresenter) {
      if (viewState instanceof RestorableParcelableViewState) {
        return new MosbyViewStateSavedState(superState, mosbyViewId,
            (RestorableParcelableViewState) viewState);
      }
      return new MosbyViewStateSavedState(superState, mosbyViewId, null);
    } else {
      return superState;
    }
  }

  /**
   * Must be called from {@link View#onRestoreInstanceState(Parcelable)}
   */
  @Override public void onRestoreInstanceState(Parcelable state) {
    if (isInEditMode) return;

    if (!(state instanceof MosbyViewStateSavedState)) {
      delegateCallback.superOnRestoreInstanceState(state);
      return;
    }

    MosbyViewStateSavedState savedState = (MosbyViewStateSavedState) state;
    mosbyViewId = savedState.getMosbyViewId();
    restoreableParcelableViewState = (VS) savedState.getRestoreableViewState();
    delegateCallback.superOnRestoreInstanceState(savedState.getSuperState());
  }
}
