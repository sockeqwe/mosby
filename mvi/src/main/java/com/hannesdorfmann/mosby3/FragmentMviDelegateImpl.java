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
import android.support.v4.app.BackstackAccessor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import java.util.UUID;

/**
 * The default implementation of {@link FragmentMviDelegate}
 * <p>
 * The View is attached to the Presenter in {@link Fragment#onStart()}.
 * So you better instantiate all your UI widgets before that lifecycle callback (typically in
 * {@link
 * Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}. The View is detached from Presenter in
 * {@link Fragment#onStop()}
 * </p>
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MviPresenter}
 * @author Hannes Dorfmann
 * @see FragmentMviDelegate
 * @since 3.0.0
 */
public class FragmentMviDelegateImpl<V extends MvpView, P extends MviPresenter<V, ?>>
    implements FragmentMviDelegate<V, P> {

  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "FragmentMviDelegateImpl";
  private static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.fragment.mvi.id";

  private String mosbyViewId = null;
  private MviDelegateCallback<V, P> delegateCallback;
  private Fragment fragment;
  private boolean onViewCreatedCalled = false;
  private final boolean keepPresenterDuringScreenOrientationChange;
  private final boolean keepPresenterOnBackstack;
  private P presenter;

  public FragmentMviDelegateImpl(@NonNull MviDelegateCallback<V, P> delegateCallback,
      @NonNull Fragment fragment) {
    this(delegateCallback, fragment, true, true);
  }

  public FragmentMviDelegateImpl(@NonNull MviDelegateCallback<V, P> delegateCallback,
      @NonNull Fragment fragment, boolean keepPresenterDuringScreenOrientationChange,
      boolean keepPresenterOnBackstack) {
    if (delegateCallback == null) {
      throw new NullPointerException("delegateCallback == null");
    }

    if (fragment == null) {
      throw new NullPointerException("fragment == null");
    }

    if (!keepPresenterDuringScreenOrientationChange && keepPresenterOnBackstack) {
      throw new IllegalArgumentException("It is not possible to keep the presenter on backstack, "
          + "but NOT keep presenter through screen orientation changes. Keep presenter on backstack also "
          + "requires keep presenter through screen orientation changes to be enabled");
    }

    this.delegateCallback = delegateCallback;
    this.fragment = fragment;
    this.keepPresenterDuringScreenOrientationChange = keepPresenterDuringScreenOrientationChange;
    this.keepPresenterOnBackstack = keepPresenterOnBackstack;
  }

  @Override public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
    onViewCreatedCalled = true;
  }

  @Override public void onStart() {

    boolean viewStateWillBeRestored = false;

    if (mosbyViewId == null) {
      // No presenter available,
      // Activity is starting for the first time (or keepPresenterDuringScreenOrientationChange == false)
      presenter = createViewIdAndCreatePresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "new Presenter instance created: " + presenter);
      }
    } else {
      presenter = PresenterManager.getPresenter(getActivity(), mosbyViewId);
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
          "MvpView returned from getMvpView() is null. Returned by " + fragment);
    }

    if (presenter == null) {
      throw new IllegalStateException(
          "Oops, Presenter is null. This seems to be a Mosby internal bug. Please report this issue here: https://github.com/sockeqwe/mosby/issues");
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

  @Override public void onActivityCreated(Bundle savedInstanceState) {

    if (!onViewCreatedCalled) {
      throw new IllegalStateException(
          "It seems that onCreateView() has never been called (or has returned null). This means that your fragment is headless (no UI). That is not allowed because it doesn't make sense to use Mosby with a Fragment without View.");
    }
  }

  private boolean retainPresenterInstance(boolean keepPresenterOnBackstack, Activity activity,
      Fragment fragment) {

    if (activity.isChangingConfigurations()) {
      if (keepPresenterDuringScreenOrientationChange) {
        return true;
      }
      return false;
    }

    if (activity.isFinishing()) {
      return false;
    }

    boolean contains = fragment.getFragmentManager().getFragments().contains(fragment);

    if (keepPresenterOnBackstack && BackstackAccessor.isFragmentOnBackStack(fragment)) {
      return true;
    }

    return !fragment.isRemoving();
  }

  @Override public void onDestroyView() {
    onViewCreatedCalled = false;
    boolean retainPresenterInstance =
        retainPresenterInstance(keepPresenterOnBackstack, getActivity(), fragment);
  }

  @Override public void onStop() {

    Activity activity = getActivity();
    boolean retainPresenterInstance =
        retainPresenterInstance(keepPresenterOnBackstack, activity, fragment);

    presenter.detachView(retainPresenterInstance);
    if (!retainPresenterInstance
        && mosbyViewId
        != null) { // mosbyViewId == null if keepPresenterDuringScreenOrientationChange == false
      PresenterManager.remove(activity, mosbyViewId);
    }

    if (DEBUG) {
      Log.d(DEBUG_TAG, "detached MvpView from Presenter. MvpView "
          + delegateCallback.getMvpView()
          + "   Presenter: "
          + presenter);
      Log.d(DEBUG_TAG, "Retaining presenter instance: "
          + Boolean.toString(retainPresenterInstance)
          + " "
          + presenter);
    }
  }

  @Override public void onCreate(@Nullable Bundle bundle) {
    if ((keepPresenterDuringScreenOrientationChange || keepPresenterOnBackstack)
        && bundle != null) {
      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);
    }

    if (DEBUG) {
      Log.d(DEBUG_TAG,
          "MosbyView ID = " + mosbyViewId + " for MvpView: " + delegateCallback.getMvpView());
    }
  }

  @Override public void onDestroy() {
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
    if (keepPresenterDuringScreenOrientationChange || keepPresenterOnBackstack) {
      Activity activity = getActivity();
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(activity, mosbyViewId, presenter);
    }
    return presenter;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if ((keepPresenterDuringScreenOrientationChange || keepPresenterOnBackstack)
        && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);

      retainPresenterInstance(keepPresenterOnBackstack, getActivity(), fragment);
      if (DEBUG) {
        Log.d(DEBUG_TAG, "Saving MosbyViewId into Bundle. ViewId: " + mosbyViewId);
      }
    }
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
