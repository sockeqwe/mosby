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

package com.hannesdorfmann.mosby3.mvi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.ActivityMviDelegate;
import com.hannesdorfmann.mosby3.FragmentMviDelegate;
import com.hannesdorfmann.mosby3.FragmentMviDelegateImpl;
import com.hannesdorfmann.mosby3.MviDelegateCallback;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * This abstract class can be used to extend from to implement an Model-View-Intent pattern with
 * this activity as View and a {@link MviPresenter} to coordinate the viewState and the underlying
 * model
 * (business logic)
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public abstract class MviFragment<V extends MvpView, P extends MviPresenter<V, ?>> extends Fragment
    implements MvpView, MviDelegateCallback<V, P> {

  private boolean isRestoringViewState = false;
  protected FragmentMviDelegate<V, P> mvpDelegate;

  @CallSuper @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getMvpDelegate().onCreate(savedInstanceState);
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    getMvpDelegate().onDestroy();
  }

  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getMvpDelegate().onSaveInstanceState(outState);
  }

  @CallSuper @Override public void onPause() {
    super.onPause();
    getMvpDelegate().onPause();
  }

  @CallSuper @Override public void onResume() {
    super.onResume();
    getMvpDelegate().onResume();
  }

  @CallSuper @Override public void onStart() {
    super.onStart();
    getMvpDelegate().onStart();
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    getMvpDelegate().onStop();
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getMvpDelegate().onViewCreated(view, savedInstanceState);
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    getMvpDelegate().onDestroyView();
  }

  @CallSuper @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getMvpDelegate().onActivityCreated(savedInstanceState);
  }

  @CallSuper @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    getMvpDelegate().onAttach(activity);
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    getMvpDelegate().onAttach(context);
  }

  @CallSuper @Override public void onDetach() {
    super.onDetach();
    getMvpDelegate().onDetach();
  }

  @CallSuper @Override public void onAttachFragment(Fragment childFragment) {
    super.onAttachFragment(childFragment);
    getMvpDelegate().onAttachFragment(childFragment);
  }

  @CallSuper @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    getMvpDelegate().onConfigurationChanged(newConfig);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this viewState
   */
  @NonNull public abstract P createPresenter();

  /**
   * Get the mvp delegate. This is internally used for creating presenter, attaching and detaching
   * viewState from presenter.
   *
   * <p><b>Please note that only one instance of mvp delegate should be used per Activity
   * instance</b>.
   * </p>
   *
   * <p>
   * Only override this method if you really know what you are doing.
   * </p>
   *
   * @return {@link ActivityMviDelegate}
   */
  @NonNull public FragmentMviDelegate<V, P> getMvpDelegate() {
    if (mvpDelegate == null) {
      mvpDelegate = new FragmentMviDelegateImpl<V, P>(this, this);
    }

    return mvpDelegate;
  }

  @NonNull @Override public V getMvpView() {
    try {
      return (V) this;
    } catch (ClassCastException e) {
      Log.e(this.toString(),
          "Couldn't cast the View to the corresponding View interface. Most likely you forgot to add \"Activity implements YourMvpViewInterface\".");
      throw e;
    }
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.isRestoringViewState = restoringViewState;
  }

  protected boolean isRestoringViewState() {
    return isRestoringViewState;
  }
}
