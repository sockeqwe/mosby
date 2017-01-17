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

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import java.util.UUID;

/**
 * The default implementation of {@link ViewGroupMviDelegate}
 *
 * @author Hannes Dorfmann
 * @see ViewGroupMviDelegate
 * @since 3.0.0
 */
public class ViewGroupMviDelegateImpl<V extends MvpView, P extends MviPresenter<V, ?>>
    implements ViewGroupMviDelegate<V, P> {

  // TODO allow custom save state hook in
  public static final boolean DEBUG = true;
  private static final String DEBUG_TAG = "ViewGroupMviDelegateImp";

  private ViewGroupMviDelegateCallback<V, P> delegateCallback;
  private String mosbyViewId;
  private final boolean keepPresenterDuringScreenOrientationChange;
  private P presenter;
  private final Activity activity;

  public ViewGroupMviDelegateImpl(ViewGroupMviDelegateCallback<V, P> delegateCallback) {
    this(delegateCallback, true);
  }

  public ViewGroupMviDelegateImpl(ViewGroupMviDelegateCallback<V, P> delegateCallback,
      boolean keepPresenterDuringScreenOrientationChange) {
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }
    this.delegateCallback = delegateCallback;
    this.keepPresenterDuringScreenOrientationChange = keepPresenterDuringScreenOrientationChange;
    this.activity = PresenterManager.getActivity(delegateCallback.getContext());
  }

  /**
   * Generates the unique (mosby internal) viewState id and calls {@link
   * MviDelegateCallback#createPresenter()}
   * to create a new presenter instance
   *
   * @return The new created presenter instance
   */
  private P createViewIdAndCreatePresenter() {

    P presenter = delegateCallback.createPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter returned from createPresenter() is null.");
    }
    if (keepPresenterDuringScreenOrientationChange) {
      Context context = delegateCallback.getContext();
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(PresenterManager.getActivity(context), mosbyViewId, presenter);
    }
    return presenter;
  }

  @NonNull private Context getContext() {
    Context c = delegateCallback.getContext();
    if (c == null) {
      throw new NullPointerException("Context returned from " + delegateCallback + " is null");
    }
    return c;
  }

  @Override public void onAttachedToWindow() {
    boolean viewStateWillBeRestored = false;

    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterInstance == false)
      presenter = createViewIdAndCreatePresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "new Presenter instance created: " + presenter);
      }
    } else {
      Context context = getContext();
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
        viewStateWillBeRestored = true;
        if (DEBUG) {
          Log.d(DEBUG_TAG, "Presenter instance reused from internal cache: " + presenter);
        }
      }
    }

    // presenter is ready, so attach viewState
    V view = delegateCallback.getMvpView();
    if (view == null) {
      throw new NullPointerException(
          "MvpView returned from getMvpView() is null. Returned by " + delegateCallback);
    }

    if (viewStateWillBeRestored) {
      delegateCallback.setRestoringViewState(true);
    }

    presenter.attachView(view);

    if (viewStateWillBeRestored) {
      delegateCallback.setRestoringViewState(false);
    }

    if (DEBUG) {
      Log.d(DEBUG_TAG,
          "MvpView attached to Presenter. MvpView: " + view + "   Presenter: " + presenter);
    }
  }

  @Override public void onDetachedFromWindow() {

    if (keepPresenterDuringScreenOrientationChange) {

      boolean destroyedPermanently = !ActivityMviDelegateImpl.retainPresenterInstance(keepPresenterDuringScreenOrientationChange, activity);

      if (destroyedPermanently) {
        // Whole activity will be destroyed
        // Internally Orientation manager already does the clean up
        if (DEBUG) {
          Log.d(DEBUG_TAG, "Detaching View "
              + delegateCallback.getMvpView()
              + " from Presenter "
              + presenter
              + " and removing presenter permanently from internal cache because the hosting Activity will be destroyed permanently");
        }

        PresenterManager.remove(activity, mosbyViewId);
        mosbyViewId = null;
        presenter.detachView(false);
      } else {
        boolean detachedBecauseOrientationChange = ActivityMviDelegateImpl.retainPresenterInstance(keepPresenterDuringScreenOrientationChange, activity);

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
      presenter.detachView(false);
      PresenterManager.remove(activity, mosbyViewId);
      mosbyViewId = null;
    }
  }

  /**
   * Must be called from {@link View#onSaveInstanceState()}
   */
  public Parcelable onSaveInstanceState() {

    Parcelable superState = delegateCallback.superOnSaveInstanceState();

    if (keepPresenterDuringScreenOrientationChange) {
      return createSavedState(superState);
    } else {
      return superState;
    }
  }

  /**
   * Create a MosbySavedState
   *
   * @return The saved state
   */
  private MosbySavedState createSavedState(Parcelable superState) {
    MosbySavedState state = new MosbySavedState(superState);
    state.setMosbyViewId(mosbyViewId);
    return state;
  }

  /**
   * Restore the data from SavedState
   *
   * @param state The state to read data from
   */
  private void restoreSavedState(MosbySavedState state) {
    mosbyViewId = state.getMosbyViewId();
  }

  /**
   * Must be called from {@link View#onRestoreInstanceState(Parcelable)}
   */
  public void onRestoreInstanceState(Parcelable state) {

    if (!(state instanceof MosbySavedState)) {
      delegateCallback.superOnRestoreInstanceState(state);
      return;
    }

    MosbySavedState savedState = (MosbySavedState) state;
    restoreSavedState(savedState);
    delegateCallback.superOnRestoreInstanceState(savedState.getSuperState());
  }
}
