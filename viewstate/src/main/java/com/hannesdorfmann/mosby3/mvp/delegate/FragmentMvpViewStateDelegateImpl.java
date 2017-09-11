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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.PresenterManager;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import java.util.UUID;

/**
 * The {@link FragmentMvpDelegateImpl} with {@link ViewState} support
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class FragmentMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
    implements FragmentMvpDelegate<V, P> {

  protected static final String KEY_MOSBY_VIEW_ID = "com.hannesdorfmann.mosby3.fragment.mvp.id";

  public static boolean DEBUG = false;
  private static final String DEBUG_TAG = "FragmentMvpDelegateImpl";
  private MvpViewStateDelegateCallback<V, P, VS> delegateCallback;
  private boolean applyViewState = false;
  private boolean applyViewStateFromMemory = false;
  protected Fragment fragment;
  protected final boolean keepPresenterInstanceDuringScreenOrientationChanges;
  protected final boolean keepPresenterOnBackstack;
  private boolean onViewCreatedCalled = false;
  protected String mosbyViewId;

  public FragmentMvpViewStateDelegateImpl(Fragment fragment,
      MvpViewStateDelegateCallback<V, P, VS> delegateCallback,
      boolean keepPresenterAndViewStateDuringScreenOrientationChange,
      boolean keepPresenterAndViewStateOnBackstack) {
    this.delegateCallback = delegateCallback;
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }

    if (fragment == null) {
      throw new NullPointerException("Fragment is null!");
    }

    if (!keepPresenterAndViewStateDuringScreenOrientationChange
        && keepPresenterAndViewStateOnBackstack) {
      throw new IllegalArgumentException("It is not possible to keep the presenter on backstack, "
          + "but NOT keep presenter through screen orientation changes. Keep presenter on backstack also "
          + "requires keep presenter through screen orientation changes to be enabled");
    }

    this.fragment = fragment;
    this.delegateCallback = delegateCallback;
    this.keepPresenterInstanceDuringScreenOrientationChanges =
        keepPresenterAndViewStateDuringScreenOrientationChange;
    this.keepPresenterOnBackstack = keepPresenterAndViewStateOnBackstack;
  }

  @Override public void onCreate(Bundle bundle) {
    if (bundle != null && keepPresenterInstanceDuringScreenOrientationChanges) {

      mosbyViewId = bundle.getString(KEY_MOSBY_VIEW_ID);

      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "MosbyView ID = " + mosbyViewId + " for MvpView: " + delegateCallback.getMvpView());
      }

      P presenter = restorePresenterOrRecreateNewPresenterAfterProcessDeath();
      delegateCallback.setPresenter(presenter);

      VS viewState = restoreViewStateOrRecreateViewStateAfterProcessDeath(bundle);
      delegateCallback.setViewState(viewState);
    } else {

      // starting for the first time
      P presenter = createViewIdAndPresenter();
      delegateCallback.setPresenter(presenter);
      VS viewState = createViewState();
      delegateCallback.setViewState(viewState);
    }

    //
    // if config change and not retaining fragment,
    // we have to do this here because if Fragment is on backstack or in ViewPager with FragmentPagerAdapter
    // a fragment (not visible) runs only the following callbacks:
    // 1. onCreate()
    // 2. onSaveInstnaceState()
    // 3. onDestroy();
    //
    // Creating the View (UI) like Fragment.onViewCreate() is not triggered if Fragment not visible.
    //
  }

  @Override public void onViewCreated(View view, Bundle bundle) {
    onViewCreatedCalled = true;
  }

  @Override public void onStart() {

    if (!onViewCreatedCalled) {
      throw new IllegalStateException("It seems that you are using "
          + delegateCallback.getClass().getCanonicalName()
          + " as headless (UI less) fragment (because onViewCreated() has not been called or maybe delegation misses that part). Having a Presenter without a View (UI) doesn't make sense. Simply use an usual fragment instead of an MvpFragment if you want to use a UI less Fragment");
    }

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
    }

    delegateCallback.getPresenter().attachView(delegateCallback.getMvpView());

    if (DEBUG) {
      Log.d(DEBUG_TAG, "View"
          + delegateCallback.getMvpView()
          + " attached to Presenter "
          + delegateCallback.getPresenter());
    }

    if (applyViewState) {
      if (!applyViewStateFromMemory && keepPresenterInstanceDuringScreenOrientationChanges) {
        if (mosbyViewId == null) {
          throw new IllegalStateException(
              "The (internal) Mosby View id is null although bundle is not null. This should never happen. This seems to be a Mosby internal error. Please report this issue at https://github.com/sockeqwe/mosby/issues");
        }
        // Put viewState from bundle into Memory Cache / Presenter Manager
        PresenterManager.putViewState(fragment.getActivity(), mosbyViewId,
            delegateCallback.getViewState());
      }
      delegateCallback.onViewStateInstanceRestored(applyViewStateFromMemory);
    } else {
      delegateCallback.onNewViewStateInstance();
    }
  }

  @Override public void onStop() {

    delegateCallback.getPresenter().detachView();

    if (DEBUG) {
      Log.d(DEBUG_TAG, "detached MvpView from Presenter. MvpView "
          + delegateCallback.getMvpView()
          + "   Presenter: "
          + delegateCallback.getPresenter());
    }

    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      // Ensure that viewstate will be applied again after backstack navigation or view pager swipe
      applyViewState = true;
      applyViewStateFromMemory = true;
    } else {
      applyViewState = false;
      applyViewStateFromMemory = false;
    }
  }

  @Override public void onDestroy() {

    Activity activity = getActivity();
    boolean retainPresenterInstance =
        FragmentMvpDelegateImpl.retainPresenterInstance(activity, fragment,
            keepPresenterInstanceDuringScreenOrientationChanges, keepPresenterOnBackstack);

    P presenter = delegateCallback.getPresenter();
    if (!retainPresenterInstance) {
      presenter.destroy();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "Presenter destroyed. MvpView "
            + delegateCallback.getMvpView()
            + "   Presenter: "
            + presenter);
      }
    }

    if (!retainPresenterInstance && mosbyViewId != null) {
      // mosbyViewId is null if keepPresenterInstanceDuringScreenOrientationChanges  == false
      PresenterManager.remove(activity, mosbyViewId);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if ((keepPresenterInstanceDuringScreenOrientationChanges || keepPresenterOnBackstack)
        && outState != null) {
      outState.putString(KEY_MOSBY_VIEW_ID, mosbyViewId);

      if (DEBUG) {
        Log.d(DEBUG_TAG, "Saving MosbyViewId into Bundle. ViewId: " + mosbyViewId);
      }
    }

    boolean keepInstance = FragmentMvpDelegateImpl.retainPresenterInstance(getActivity(), fragment,
        keepPresenterInstanceDuringScreenOrientationChanges, keepPresenterOnBackstack);
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

  @Override public void onDestroyView() {
    onViewCreatedCalled = false;
  }

  @Override public void onPause() {
  }

  @Override public void onResume() {
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
  }

  @Override public void onAttach(Activity activity) {
  }

  @Override public void onDetach() {
  }

  /**
   * Creates the presenter instance if not able to reuse presenter from PresenterManager
   */
  private P restorePresenterOrRecreateNewPresenterAfterProcessDeath() {

    P presenter;

    if (keepPresenterInstanceDuringScreenOrientationChanges) {

      if (mosbyViewId != null
          && (presenter = PresenterManager.getPresenter(getActivity(), mosbyViewId)) != null) {
        //
        // Presenter restored from cache
        //
        if (DEBUG) {
          Log.d(DEBUG_TAG,
              "Reused presenter " + presenter + " for view " + delegateCallback.getMvpView());
        }

        return presenter;
      } else {
        //
        // No presenter found in cache, most likely caused by process death
        //
        presenter = createViewIdAndPresenter();
        if (DEBUG) {
          Log.d(DEBUG_TAG, "No presenter found although view Id was here: "
              + mosbyViewId
              + ". Most likely this was caused by a process death. New Presenter created"
              + presenter
              + " for view "
              + delegateCallback.getMvpView());
        }

        return presenter;
      }
    } else {
      //
      // starting first time, so create a new presenter
      //
      presenter = createViewIdAndPresenter();
      if (DEBUG) {
        Log.d(DEBUG_TAG,
            "New presenter " + presenter + " for view " + delegateCallback.getMvpView());
      }
      return presenter;
    }
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
   * Generates the unique (mosby internal) view id and calls {@link
   * MvpDelegateCallback#createPresenter()}
   * to create a new presenter instance
   *
   * @return The new created presenter instance
   */
  private P createViewIdAndPresenter() {

    P presenter = delegateCallback.createPresenter();
    if (presenter == null) {
      throw new NullPointerException(
          "Presenter returned from createPresenter() is null. Fragment is " + fragment);
    }

    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      mosbyViewId = UUID.randomUUID().toString();
      PresenterManager.putPresenter(getActivity(), mosbyViewId, presenter);
    }

    return presenter;
  }

  /**
   * Creates a new ViewState instance
   *
   * @return the newly created instance
   */
  private VS createViewState() {
    VS viewState = delegateCallback.createViewState();
    if (viewState == null) {
      throw new NullPointerException(
          "ViewState returned from createViewState() is null. Fragment is " + fragment);
    }

    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      PresenterManager.putViewState(getActivity(), mosbyViewId, viewState);
    }

    return viewState;
  }

  private VS restoreViewStateOrRecreateViewStateAfterProcessDeath(Bundle bundle) {

    if (bundle == null) {
      throw new NullPointerException("Bundle is null. This should never be the case"
          + "Please report this issue at https://github.com/sockeqwe/mosby/issues");
    }

    if (mosbyViewId == null) {
      throw new NullPointerException(
          "The (internal) Mosby View id is null although bundle is not null. "
              + "This should never be the case while restoring ViewState instance. "
              + "Please report this issue at https://github.com/sockeqwe/mosby/issues");
    }

    //
    // Try to restore ViewState from PresenterManager
    //
    VS viewState = PresenterManager.getViewState(fragment.getActivity(), mosbyViewId);
    if (viewState != null) {
      applyViewState = true;
      applyViewStateFromMemory = true;
      if (DEBUG) {
        Log.d(DEBUG_TAG, "ViewState reused from Mosby internal cache for view: "
            + delegateCallback.getMvpView()
            + " viewState: "
            + viewState);
      }

      return viewState;
    }

    //
    // Try to restore viewstate from bundle
    //
    viewState = delegateCallback.createViewState();
    if (viewState == null) {
      throw new NullPointerException(
          "ViewState returned from createViewState() is null! MvpView that has returned null as ViewState is: "
              + delegateCallback.getMvpView());
    }

    if (viewState instanceof RestorableViewState) {
      // A little bit hacky that we need an instance of the viewstate to restore a view state
      // (may creates another view state object) but I don't know any better way :)
      RestorableViewState restoredViewState =
          ((RestorableViewState) viewState).restoreInstanceState(bundle);

      if (restoredViewState != null) {
        //
        // ViewState restored from bundle
        //
        viewState = (VS) restoredViewState;
        applyViewState = true;
        applyViewStateFromMemory = false;

        if (keepPresenterInstanceDuringScreenOrientationChanges) {
          PresenterManager.putViewState(getActivity(), mosbyViewId, viewState);
        }

        if (DEBUG) {
          Log.d(DEBUG_TAG, "Recreated ViewState from bundle for view: "
              + delegateCallback.getMvpView()
              + " viewState: "
              + viewState);
        }

        return viewState;
      }
    }

    //
    // Entirely new ViewState has been created, typically because process death and mosby view id points to
    // a  old id but view got a new one because of process death.
    //

    applyViewState = false;
    applyViewStateFromMemory = false;

    if (keepPresenterInstanceDuringScreenOrientationChanges) {
      PresenterManager.putViewState(getActivity(), mosbyViewId, viewState);
    }

    if (DEBUG) {
      Log.d(DEBUG_TAG, "Created a new ViewState instance for view: "
          + delegateCallback.getMvpView()
          + " viewState: "
          + viewState);
    }

    return viewState;
  }
}
