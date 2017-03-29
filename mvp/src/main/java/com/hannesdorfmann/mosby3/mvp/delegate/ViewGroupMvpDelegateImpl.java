/*
 * Copyright 2017 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.mvp.delegate;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.hannesdorfmann.mosby3.MosbySavedState;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;

/**
 * The mvp delegate used for everything that derives from {@link View} like {@link FrameLayout}
 * etc.
 *
 * <p>
 * The following methods must be called from the corresponding View lifecycle method:
 * <ul>
 * <li>{@link #onAttachedToWindow()}</li>
 * <li>{@link #onDetachedFromWindow()}</li>
 * </ul>
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class ViewGroupMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    implements ViewGroupMvpDelegate<V, P> {

  // TODO allow custom save state hook in

  @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL", justification = "Could be set for debugging")
  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "ViewGroupMvpDelegateImp";

  private ViewGroupDelegateCallback<V, P> delegateCallback;
  private String mosbyViewId;
  private final boolean keepPresenterDuringScreenOrientationChange;
  private final Activity activity;
  private final boolean isInEditMode;

  public ViewGroupMvpDelegateImpl(@NonNull View view,
      @NonNull ViewGroupDelegateCallback<V, P> delegateCallback,
      boolean keepPresenterDuringScreenOrientationChange) {
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }

    this.delegateCallback = delegateCallback;
    this.keepPresenterDuringScreenOrientationChange = keepPresenterDuringScreenOrientationChange;

    isInEditMode = view.isInEditMode();

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
  protected P createViewIdAndCreatePresenter() {

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
    if (isInEditMode) return;

    P presenter = null;
    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterInstance == false)
      presenter = createViewIdAndCreatePresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "new Presenter instance created: " + presenter);
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

    if (presenter == null) {
      throw new IllegalStateException(
          "Oops, Presenter is null. This seems to be a Mosby internal bug. Please report this issue here: https://github.com/sockeqwe/mosby/issues");
    }

    /*
    if (viewStateWillBeRestored) {
      delegateCallback.setRestoringViewState(true);
    }
    */

    delegateCallback.setPresenter(presenter);
    presenter.attachView(view);

    /*
    if (viewStateWillBeRestored) {
      delegateCallback.setRestoringViewState(false);
    }
    */

    if (DEBUG) {
      Log.d(DEBUG_TAG,
          "MvpView attached to Presenter. MvpView: " + view + "   Presenter: " + presenter);
    }
  }

  @Override public void onDetachedFromWindow() {

    if (isInEditMode) return;

    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      throw new NullPointerException(
          "Presenter returned from delegateCallback.getPresenter() is null");
    }

    if (keepPresenterDuringScreenOrientationChange) {

      boolean destroyedPermanently = !ActivityMvpDelegateImpl.retainPresenterInstance(
          keepPresenterDuringScreenOrientationChange, activity);

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
        boolean detachedBecauseOrientationChange = ActivityMvpDelegateImpl.retainPresenterInstance(
            keepPresenterDuringScreenOrientationChange, activity);

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
    if (isInEditMode) return null;

    Parcelable superState = delegateCallback.superOnSaveInstanceState();

    if (keepPresenterDuringScreenOrientationChange) {
      return new MosbySavedState(superState, mosbyViewId);
    } else {
      return superState;
    }
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
    if (isInEditMode) return;

    if (!(state instanceof MosbySavedState)) {
      delegateCallback.superOnRestoreInstanceState(state);
      return;
    }

    MosbySavedState savedState = (MosbySavedState) state;
    restoreSavedState(savedState);
    delegateCallback.superOnRestoreInstanceState(savedState.getSuperState());
  }
}
