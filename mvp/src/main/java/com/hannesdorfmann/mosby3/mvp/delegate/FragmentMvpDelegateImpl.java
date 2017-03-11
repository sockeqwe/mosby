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
import android.support.annotation.Nullable;
import android.support.v4.app.BackstackAccessor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;

/**
 * * The default implementation of {@link FragmentMvpDelegate}
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @see FragmentMvpDelegate
 * @since 1.1.0
 */
public class FragmentMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    implements FragmentMvpDelegate<V, P> {

  protected static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.fragment.mvp.id";


  @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL", justification = "Could be enabled for debugging purpose")
  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "FragmentMvpVSDelegate";

  private MvpDelegateCallback<V, P> delegateCallback;
  protected Fragment fragment;
  protected final boolean keepPresenterInstanceDuringScreenOrientationChanges;
  protected final boolean keepPresenterOnBackstack;
  private boolean onViewCreatedCalled = false;
  protected String mosbyViewId;

  /**
   * @param fragment The Fragment
   * @param delegateCallback the DelegateCallback
   * @param keepPresenterDuringScreenOrientationChange true, if the presenter should be kept during
   * screen orientation
   * changes. Otherwise, false
   * @param keepPresenterOnBackstack true, if the presenter should be kept when the fragment is
   * destroyed because it is put on the backstack, Otherwise false
   */
  public FragmentMvpDelegateImpl(@NonNull Fragment fragment,
      @NonNull MvpDelegateCallback<V, P> delegateCallback,
      boolean keepPresenterDuringScreenOrientationChange, boolean keepPresenterOnBackstack) {
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }

    if (fragment == null) {
      throw new NullPointerException("Fragment is null!");
    }

    if (!keepPresenterDuringScreenOrientationChange && keepPresenterOnBackstack) {
      throw new IllegalArgumentException("It is not possible to keep the presenter on backstack, "
          + "but NOT keep presenter through screen orientation changes. Keep presenter on backstack also "
          + "requires keep presenter through screen orientation changes to be enabled");
    }

    this.fragment = fragment;
    this.delegateCallback = delegateCallback;
    this.keepPresenterInstanceDuringScreenOrientationChanges =
        keepPresenterDuringScreenOrientationChange;
    this.keepPresenterOnBackstack = keepPresenterOnBackstack;
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
          "Presenter returned from createPresenter() is null. Activity is " + getActivity());
    }
    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(getActivity(), mosbyViewId, presenter);
    }
    return presenter;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle bundle) {

    P presenter = null;

    if (bundle != null && keepPresenterInstanceDuringScreenOrientationChanges) {

      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);

      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "MosbyView ID = " + mosbyViewId + " for MvpView: " + delegateCallback.getMvpView());
      }

      if (mosbyViewId != null
          && (presenter = PresenterManager.getPresenter(getActivity(), mosbyViewId)) != null) {
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

    onViewCreatedCalled = true;
  }

  @NonNull private Activity getActivity() {
    Activity activity = fragment.getActivity();
    if (activity == null) {
      throw new NullPointerException(
          "Activity returned by Fragment.getActivity() is null. Fragment is " + fragment);
    }

    return activity;
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

  protected boolean retainPresenterInstance() {

    Activity activity = getActivity();
    if (activity.isChangingConfigurations()) {
      return keepPresenterInstanceDuringScreenOrientationChanges;
    }

    if (activity.isFinishing()) {
      return false;
    }

    if (keepPresenterOnBackstack && BackstackAccessor.isFragmentOnBackStack(fragment)) {
      return true;
    }

    return !fragment.isRemoving();
  }

  @Override public void onDestroyView() {

    onViewCreatedCalled = false;

    Activity activity = getActivity();
    boolean retainPresenterInstance = retainPresenterInstance();

    P presenter = getPresenter();
    presenter.detachView(retainPresenterInstance);
    if (!retainPresenterInstance) {
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

  @Override public void onPause() {

  }

  @Override public void onResume() {

  }

  @Override public void onStart() {

    if (!onViewCreatedCalled) {
      throw new IllegalStateException("It seems that you are using "
          + delegateCallback.getClass().getCanonicalName()
          + " as headless (UI less) fragment (because onViewCreated() has not been called or maybe delegation misses that part). Having a Presenter without a View (UI) doesn't make sense. Simply use an usual fragment instead of an MvpFragment if you want to use a UI less Fragment");
    }
  }

  @Override public void onStop() {

  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {

  }

  @Override public void onAttach(Activity activity) {

  }

  @Override public void onDetach() {

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if ((keepPresenterInstanceDuringScreenOrientationChanges || keepPresenterOnBackstack)
        && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);

      if (DEBUG) {
        Log.d(DEBUG_TAG, "Saving MosbyViewId into Bundle. ViewId: " + mosbyViewId);
      }
    }
  }

  @Override public void onCreate(Bundle saved) {
  }

  @Override public void onDestroy() {
  }
}
