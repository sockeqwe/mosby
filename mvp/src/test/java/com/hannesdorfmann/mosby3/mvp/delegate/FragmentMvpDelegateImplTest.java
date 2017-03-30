/*
 *  Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.mvp.delegate;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */
@RunWith(PowerMockRunner.class) @PrepareForTest({ Fragment.class })
public class FragmentMvpDelegateImplTest {

  // TODO write test for retaining fragment

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private MvpDelegateCallback<MvpView, MvpPresenter<MvpView>> callback;
  private FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate;
  private Fragment fragment;
  private FragmentActivity activity;
  private Application application;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();

    fragment = PowerMockito.mock(Fragment.class);
    activity = Mockito.mock(FragmentActivity.class);
    application = Mockito.mock(Application.class);

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(fragment.getActivity()).thenReturn(activity);

    Mockito.when(activity.getApplication()).thenReturn(application);

    delegate = new FragmentMvpDelegateImpl<>(fragment, callback, true, true);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
    startFragment(delegate, null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(delegate, bundle, 1, true, true, false);
    startFragment(delegate, bundle, 1, 2, 2);
    finishFragment(delegate, bundle, 1, false, false, true);
  }

  @Test public void appStartFinishing() {
    startFragment(delegate, null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(delegate, bundle, 1, false, false, true);
  }

  @Test public void dontKeepPresenter() {
    delegate = new FragmentMvpDelegateImpl<>(fragment, callback, false, false);
    startFragment(delegate, null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(delegate, bundle, 1, false, true, false);
    startFragment(delegate, null, 2, 2, 2);
    finishFragment(delegate, bundle, 2, false, false, true);
  }

  /**
   * Checks if two Fragments one that keeps presenter, the other who doesn't keep presenter during
   * screen orientation changes work properly
   *
   * https://github.com/sockeqwe/mosby/issues/231
   */
  @Test public void dontKeepPresenterWithSecondFragmentInPresenterManager() {

    MvpView view1 = new MvpView() {
    };

    MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
    Fragment fragment1 = PowerMockito.mock(Fragment.class);
    PartialMvpDelegateCallbackImpl callback1 = Mockito.mock(PartialMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
    Mockito.doCallRealMethod().when(callback1).getPresenter();
    Mockito.when(callback1.getMvpView()).thenReturn(view1);
    Mockito.when(fragment1.getActivity()).thenReturn(activity);
    Mockito.when(callback1.createPresenter()).thenReturn(presenter1);

    FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> keepDelegate =
        new FragmentMvpDelegateImpl<>(fragment1, callback1, true, false);

    startFragment(keepDelegate, null);

    FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> dontKeepDelegate =
        new FragmentMvpDelegateImpl<>(fragment, callback, false, false);


    startFragment(dontKeepDelegate, null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(dontKeepDelegate, bundle, 1, false, true, false);
    startFragment(dontKeepDelegate, null, 2, 2, 2);
    finishFragment(dontKeepDelegate, bundle, 2, false, false, true);

    Bundle bundle2 = BundleMocker.create();
    finishFragment(keepDelegate, bundle2);
  }

  private void startFragment(FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate,
      Bundle bundle, int createPresenter, int setPresenter, int attachView) {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);

    startFragment(delegate, bundle);

    Mockito.verify(callback, Mockito.times(createPresenter)).createPresenter();
    Mockito.verify(callback, Mockito.times(setPresenter)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(attachView)).attachView(view);
  }

  private void startFragment(FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate,
      Bundle bundle) {
    delegate.onAttach(activity);
    delegate.onCreate(bundle);
    delegate.onViewCreated(null, bundle);
    delegate.onActivityCreated(bundle);
    delegate.onStart();
    delegate.onResume();
  }

  private void finishFragment(FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate,
      Bundle bundle) {
    delegate.onPause();
    delegate.onSaveInstanceState(bundle);
    delegate.onStop();
    delegate.onDestroyView();
    delegate.onDestroy();
    delegate.onDetach();
  }

  private void finishFragment(FragmentMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate,
      Bundle bundle, int detachViewCount, boolean expectKeepPresenter,
      boolean changingConfigurations, boolean isFinishing) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(changingConfigurations);
    Mockito.when(activity.isFinishing()).thenReturn(isFinishing);

    finishFragment(delegate, bundle);

    Mockito.verify(presenter, Mockito.times(detachViewCount)).detachView(expectKeepPresenter);
  }
}
