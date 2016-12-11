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

package com.hannesdorfmann.mosby3;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * The concrete implementation of {@link ActivityMviDelegate}.
 * This delegate creates the Presenter and attaches the view to the presenter in {@link
 * Activity#onStart()}. The view is detached from presenter in {@link
 * Activity#onStop()}
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @see ActivityMviDelegate
 * @since 3.0
 */
public class ActivityMviDelegateImpl<V extends MvpView, P extends MviPresenter<V, ?>>
    implements ActivityMviDelegate {

  private static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.activity.view.id";
  private String mosbyViewId = null;

  private MviDelegateCallback<V, P> delegateCallback;
  private PresenterManager<V, P> presenterManager = new PresenterManager<V, P>();
  private Activity activity;
  private boolean keepPresenterInstance;
  private P presenter;

  /**
   * Creates a new Delegate with an presenter that survives screen orientation changes
   *
   * @param activity The activity
   * @param delegateCallback The delegate callback
   */
  public ActivityMviDelegateImpl(@NonNull Activity activity,
      @NonNull MviDelegateCallback<V, P> delegateCallback) {
    this(activity, delegateCallback, true);
  }

  /**
   * Creates a new delegate
   *
   * @param activity The activity
   * @param delegateCallback The delegate callback
   * @param keepPresenterInstance true, if the presenter instance should be kept through screen
   * orientation changes, false if not (a new presenter instance will be created every time you
   * rotate your device)
   */
  public ActivityMviDelegateImpl(@NonNull Activity activity,
      @NonNull MviDelegateCallback<V, P> delegateCallback, boolean keepPresenterInstance) {
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }
    this.delegateCallback = delegateCallback;
    this.activity = activity;
    this.keepPresenterInstance = keepPresenterInstance;
  }

  @Override public void onCreate(@Nullable Bundle bundle) {
    if (keepPresenterInstance && bundle != null) {
      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);
    }
  }

  @Override public void onStart() {
    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterInstance == false)
      presenter = createViewIdAndCreatePresenter();
    } else {
      presenter = presenterManager.getPresenter(mosbyViewId, activity);
      if (presenter == null) {
        // Process death,
        // hence no presenter with the given view id stored, although we have a view id
        presenter = createViewIdAndCreatePresenter();
      }
    }

    // presenter is ready, so attach view
    V view = delegateCallback.getMvpView();
    if (view == null) {
      throw new NullPointerException(
          "MvpView returned from getMvpView() is null. Returned by " + activity);
    }
    presenter.attachView(view);
  }

  /**
   * Generates the unique (mosby internal) view id and calls {@link MviDelegateCallback#createPresenter()}
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
      mosbyViewId = presenterManager.nextViewId(activity);
      presenterManager.putPresenter(mosbyViewId, presenter, activity);
    }
    return presenter;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (keepPresenterInstance && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);
    }
  }

  @Override public void onStop() {
    boolean destroyPresenter = keepPresenterInstance && activity.isFinishing();
    Log.d("ActivityMviDelegateImpl", "onStop() "+destroyPresenter);
    presenter.detachView(destroyPresenter);
    if (destroyPresenter) {
      presenterManager.removePresenterAndViewState(mosbyViewId, activity);
    }
    presenterManager.cleanUp();
    presenterManager = null;
    presenter = null;
    activity = null;
    delegateCallback = null;
  }

  @Override public void onDestroy() {
  }

  @Override public void onPostCreate(Bundle savedInstanceState) {
  }

  @Override public void onPause() {
  }

  @Override public void onResume() {
  }

  @Override public void onRestart() {
  }

  @Override public void onContentChanged() {
  }

  @Override public Object onRetainCustomNonConfigurationInstance() {
    return null;
  }
}
