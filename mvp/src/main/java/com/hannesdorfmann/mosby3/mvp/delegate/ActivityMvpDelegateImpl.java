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
import java.util.UUID;

/**
 * The concrete implementation of {@link}
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @see ActivityMvpDelegate
 * @since 1.1.0
 */
public class ActivityMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    implements ActivityMvpDelegate {

  protected static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.activity.mvp.id";

  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "ActivityMvpDelegateImpl";

  private MvpDelegateCallback<V, P> delegateCallback;
  protected boolean keepPresenterInstance;
  protected Activity activity;
  protected String mosbyViewId = null;

  /**
   * @param activity The Activity
   * @param delegateCallback The callback
   * @param keepPresenterInstance true, if the presenter instance should be kept across screen
   * orientation changes. Otherwise false.
   */
  public ActivityMvpDelegateImpl(@NonNull Activity activity,
      @NonNull MvpDelegateCallback<V, P> delegateCallback, boolean keepPresenterInstance) {

    if (activity == null) {
      throw new NullPointerException("Activity is null!");
    }

    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }
    this.delegateCallback = delegateCallback;
    this.activity = activity;
    this.keepPresenterInstance = keepPresenterInstance;
  }

  /**
   * Determines whether or not a Presenter Instance should be kept
   *
   * @param keepPresenterInstance true, if the delegate has enabled keep
   */
  static boolean retainPresenterInstance(boolean keepPresenterInstance, Activity activity) {
    return keepPresenterInstance && (activity.isChangingConfigurations()
        || !activity.isFinishing());
  }

  /**
   * Generates the unique (mosby internal) view id and calls {@link
   * MvpDelegateCallback#createPresenter()}
   * to create a new presenter instance
   *
   * @return The new created presenter instance
   */
  private P createViewIdAndCreatePresenter() {

    P presenter = delegateCallback.createPresenter();
    if (presenter == null) {
      throw new NullPointerException(
          "Presenter returned from createPresenter() is null. Activity is " + activity);
    }
    if (keepPresenterInstance) {
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(activity, mosbyViewId, presenter);
    }
    return presenter;
  }

  @Override public void onCreate(Bundle bundle) {

    P presenter = null;

    if (bundle != null && keepPresenterInstance) {

      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);

      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "MosbyView ID = " + mosbyViewId + " for MvpView: " + delegateCallback.getMvpView());
      }

      if (mosbyViewId != null
          && (presenter = PresenterManager.getPresenter(activity, mosbyViewId)) != null) {
        //
        // Presenter restored from cache
        //
        if (DEBUG) {
          Log.d(DEBUG_TAG,
              "Reused presenter " + presenter + " for view " + delegateCallback.getMvpView());
        }
      } else {
        //
        // No presenter found in cache, most likely caused by process death
        //
        presenter = createViewIdAndCreatePresenter();
        if (DEBUG) {
          Log.d(DEBUG_TAG, "No presenter found although view Id was here: "
              + mosbyViewId
              + ". Most likely this was caused by a process death. New Presenter created"
              + presenter
              + " for view "
              + getMvpView());
        }
      }
    } else {
      //
      // Activity starting first time, so create a new presenter
      //
      presenter = createViewIdAndCreatePresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "New presenter " + presenter + " for view " + getMvpView());
      }
    }

    if (presenter == null) {
      throw new IllegalStateException(
          "Oops, Presenter is null. This seems to be a Mosby internal bug. Please report this issue here: https://github.com/sockeqwe/mosby/issues");
    }

    delegateCallback.setPresenter(presenter);
    getPresenter().attachView(getMvpView());

    if (DEBUG) {
      Log.d(DEBUG_TAG, "View" + getMvpView() + " attached to Presenter " + presenter);
    }
  }

  private P getPresenter() {
    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter returned from getPresenter() is null");
    }
    return presenter;
  }

  private V getMvpView() {
    V view = delegateCallback.getMvpView();
    if (view == null) {
      throw new NullPointerException("View returned from getMvpView() is null");
    }
    return view;
  }

  @Override public void onDestroy() {
    boolean retainPresenterInstance = retainPresenterInstance(keepPresenterInstance, activity);
    getPresenter().detachView(retainPresenterInstance);
    if (!retainPresenterInstance && mosbyViewId != null) {
      PresenterManager.remove(activity, mosbyViewId);
    }

    if (DEBUG) {
      if (retainPresenterInstance) {
        Log.d(DEBUG_TAG, "View"
            + getMvpView()
            + " destroyed temporarily. View detached from presenter "
            + getPresenter());
      } else {
        Log.d(DEBUG_TAG, "View"
            + getMvpView()
            + " destroyed permanently. View detached permanently from presenter "
            + getPresenter());
      }
    }
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {

  }

  @Override public void onStart() {

  }

  @Override public void onStop() {

  }

  @Override public void onRestart() {

  }

  @Override public void onContentChanged() {

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (keepPresenterInstance && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);
      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "Saving MosbyViewId into Bundle. ViewId: " + mosbyViewId + " for view " + getMvpView());
      }
    }
  }

  @Override public void onPostCreate(Bundle savedInstanceState) {
  }
}
