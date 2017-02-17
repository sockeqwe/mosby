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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Fragment.class})
public class FragmentMvpDelegateImplTest {

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

    delegate = new FragmentMvpDelegateImpl<>(fragment, callback);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
    startFragment(null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, true, true, false);
    startFragment(bundle, 1, 2, 2);
    finishFragment(bundle, 1, false, false, true);
  }

  @Test public void appStartFinishing() {
    startFragment(null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, false, false, true);
  }

  @Test public void dontKeepPresenter() {
    delegate = new FragmentMvpDelegateImpl<>(fragment, callback, false, false);
    startFragment(null, 1, 1, 1);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, false, true, false);
    startFragment(null, 2, 2, 2);
    finishFragment(bundle, 2, false, false, true);
  }

  private void startFragment(Bundle bundle, int createPresenter, int setPresenter, int attachView) {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);

    startFragment(bundle);

    Mockito.verify(callback, Mockito.times(createPresenter)).createPresenter();
    Mockito.verify(callback, Mockito.times(setPresenter)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(attachView)).attachView(view);
  }

  private void startFragment(Bundle bundle) {
    delegate.onAttach(activity);
    delegate.onCreate(bundle);
    delegate.onViewCreated(null, bundle);
    delegate.onActivityCreated(bundle);
    delegate.onStart();
    delegate.onResume();
  }

  private void finishFragment(Bundle bundle, int detachViewCount, boolean expectKeepPresenter,
      boolean changingConfigurations, boolean isFinishing) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(changingConfigurations);
    Mockito.when(activity.isFinishing()).thenReturn(isFinishing);

    delegate.onPause();
    delegate.onSaveInstanceState(bundle);
    delegate.onStop();
    delegate.onDestroyView();
    delegate.onDestroy();
    delegate.onDetach();

    Mockito.verify(presenter, Mockito.times(detachViewCount)).detachView(expectKeepPresenter);
  }
}
