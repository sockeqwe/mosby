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
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * * The default implementation of {@link FragmentMviDelegate}
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MviPresenter}
 * @author Hannes Dorfmann
 * @see FragmentMviDelegate
 * @since 3.0.0
 */
public class FragmentMviDelegateImpl<V extends MvpView, P extends MviPresenter<V, ?>>
    implements FragmentMviDelegate<V, P> {

  private static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.activity.viewState.id";
  private String mosbyViewId = null;

  private PresenterManager<V, P> presenterManager = new PresenterManager<V, P>();

  private MviDelegateCallback<V, P> delegateCallback;
  private Fragment fragment;
  private boolean onViewCreatedCalled = false;
  private final boolean keepPresenterInstance;
  private P presenter;

  public FragmentMviDelegateImpl(@NonNull MviDelegateCallback<V, P> delegateCallback,
      @NonNull Fragment fragment) {
    this(delegateCallback, fragment, true);
  }

  public FragmentMviDelegateImpl(@NonNull MviDelegateCallback<V, P> delegateCallback,
      @NonNull Fragment fragment, boolean keepPresenterInstance) {
    if (delegateCallback == null) {
      throw new NullPointerException("delegateCallback == null");
    }

    if (fragment == null) {
      throw new NullPointerException("fragment == null");
    }
    this.delegateCallback = delegateCallback;
    this.fragment = fragment;
    this.keepPresenterInstance = keepPresenterInstance;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    onViewCreatedCalled = true;
  }

  @Override public void onDestroyView() {
    onViewCreatedCalled = false;
  }

  @Override public void onCreate(@Nullable Bundle bundle) {
    if (keepPresenterInstance && bundle != null) {
      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);
    }
  }

  @Override public void onDestroy() {
    presenterManager = null;
    presenter = null;
    delegateCallback = null;
    fragment = null;
  }

  @Override public void onPause() {
  }

  @Override public void onResume() {
  }

  @NonNull private Activity getActivity() {
    Activity activity = fragment.getActivity();
    if (activity == null) {
      throw new NullPointerException(
          "Activity returned by Fragment.getActivity() is null. Fragment is " + fragment);
    }

    return activity;
  }

  @Override public void onStart() {
    if (!onViewCreatedCalled) {
      throw new IllegalStateException(
          "It seems that onCreateView() has never been called (or has returned null). This means that your fragment is headless (no UI). That is not allowed because it doesn't make sense to use Mosby with a Fragment without View.");
    }

    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterInstance == false)
      presenter = createViewIdAndCreatePresenter();
    } else {
      presenter = presenterManager.getPresenter(mosbyViewId, getActivity());
      if (presenter == null) {
        // Process death,
        // hence no presenter with the given viewState id stored, although we have a viewState id
        presenter = createViewIdAndCreatePresenter();
      }
    }

    // presenter is ready, so attach viewState
    V view = delegateCallback.getMvpView();
    if (view == null) {
      throw new NullPointerException(
          "MvpView returned from getMvpView() is null. Returned by " + fragment);
    }
    presenter.attachView(view);
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
      throw new NullPointerException(
          "Presenter returned from createPresenter() is null. Fragment is " + fragment);
    }
    if (keepPresenterInstance) {
      Context context = getActivity();
      mosbyViewId = presenterManager.nextViewId(context);
      presenterManager.putPresenter(mosbyViewId, presenter, context);
    }
    return presenter;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (keepPresenterInstance && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);
    }
  }

  @Override public void onStop() {
    Activity activity = getActivity();
    boolean destroyPresenter = keepPresenterInstance && activity.isFinishing();
    presenter.detachView(destroyPresenter);
    if (destroyPresenter) {
      presenterManager.removePresenterAndViewState(mosbyViewId, activity);
    }

    presenterManager.cleanUp();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
  }

  @Override public void onAttach(Activity activity) {
  }

  @Override public void onDetach() {
  }

  @Override public void onAttach(Context context) {
  }

  @Override public void onAttachFragment(Fragment childFragment) {
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {

  }
}
