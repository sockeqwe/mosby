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

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */
@RunWith(PowerMockRunner.class) @PrepareForTest({ Fragment.class })
public class FragmentMvpViewStateDelegateImplTest {

  // TODO write test for retaining fragment

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private MvpViewStateDelegateCallback<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> callback;
  private FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>>
      delegate;
  private Fragment fragment;
  private FragmentActivity activity;
  private Application application;
  private ViewState<MvpView> viewState;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    viewState = Mockito.mock(ViewState.class);

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.spy(PartialMvpViewStateDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.doCallRealMethod().when(callback).getViewState();

    fragment = PowerMockito.mock(Fragment.class);
    activity = Mockito.mock(FragmentActivity.class);
    application = Mockito.mock(Application.class);

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(fragment.getActivity()).thenReturn(activity);

    Mockito.when(activity.getApplication()).thenReturn(application);

    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);

    delegate = new FragmentMvpViewStateDelegateImpl<>(fragment, callback, true, true);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
    startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1,0,  true, false);
    startFragment(bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
    finishFragment(bundle, 2, 1, false, true);
  }

  @Test public void appStartFinishing() {
    startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, 1, false, true);
    Mockito.verifyNoMoreInteractions(viewState);
  }

  @Test public void dontKeepPresenter() {
    delegate = new FragmentMvpViewStateDelegateImpl<>(fragment, callback, false, false);
    startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, 1, true, false);
    startFragment(null, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
    finishFragment(bundle, 2, 2, false, true);
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
    ViewState<MvpView> viewState1 = Mockito.mock(ViewState.class);

    MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
    PartialMvpViewStateDelegateCallbackImpl callback1 =
        Mockito.spy(PartialMvpViewStateDelegateCallbackImpl.class);
    Fragment fragment1 = PowerMockito.mock(Fragment.class);

    Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
    Mockito.doCallRealMethod().when(callback1).getPresenter();
    Mockito.doCallRealMethod().when(callback1).setViewState(viewState1);
    Mockito.doCallRealMethod().when(callback1).getViewState();
    Mockito.when(callback1.getMvpView()).thenReturn(view1);
    Mockito.when(fragment1.getActivity()).thenReturn(activity);
    Mockito.when(callback1.createPresenter()).thenReturn(presenter1);
    Mockito.when(callback1.createViewState()).thenReturn(viewState1);

    FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>>
        keepDelegate = new FragmentMvpViewStateDelegateImpl<>(fragment1, callback1, true, false);

    startFragment(keepDelegate, null);

    delegate = new FragmentMvpViewStateDelegateImpl<>(fragment, callback, false, false);
    startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, 1, true, false);
    startFragment(null, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
    finishFragment(bundle, 2, 2, false, true);

    finishFragment(keepDelegate, BundleMocker.create());
  }

  @Test public void appStartAfterProcessDeathAndViewStateRecreationFromBundle() {
    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        viewState = Mockito.spy(new SimpleRestorableViewState());
        return viewState;
      }
    }).when(callback).createViewState();

    Bundle bundle = BundleMocker.create();
    bundle.putString(FragmentMvpViewStateDelegateImpl.KEY_MOSBY_VIEW_ID, "123456789");

    startFragment(bundle, 1, 1, 1, 1, 1, 1, false, 1, 0, 1);
  }

  @Test public void appStartWithViewStateFromMemoryAndBundleShouldPreferViewStateFromMemory() {

    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        viewState = Mockito.spy(new SimpleRestorableViewState());
        return viewState;
      }
    }).when(callback).createViewState();

    startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishFragment(bundle, 1, 0, true, false);
    startFragment(bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
    finishFragment(bundle, 2, 1, false, true);
  }

  private void startFragment(
      FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate,
      Bundle bundle) {

    delegate.onAttach(activity);
    delegate.onCreate(bundle);
    delegate.onViewCreated(null, bundle);
    delegate.onActivityCreated(bundle);
    delegate.onStart();
    delegate.onResume();
  }

  private void startFragment(Bundle bundle, int createPresenter, int setPresenter, int attachView,
      int createViewState, int setViewState, int applyViewState,
      Boolean viewsStateRestoredFromMemory, int setRestoreViewState, int onNewViewStateInstance,
      int onViewStateInstanceRestored) {

    startFragment(delegate, bundle);

    Mockito.verify(callback, Mockito.times(createPresenter)).createPresenter();
    Mockito.verify(callback, Mockito.times(setPresenter)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(attachView)).attachView(view);

    Mockito.verify(callback, Mockito.times(createViewState)).createViewState();
    Mockito.verify(callback, Mockito.times(setViewState)).setViewState(viewState);

    if (viewsStateRestoredFromMemory == null) {
      Mockito.verify(viewState, Mockito.times(0)).apply(Mockito.eq(view), Mockito.eq(true));
      Mockito.verify(viewState, Mockito.times(0)).apply(Mockito.eq(view), Mockito.eq(false));
    } else {
      Mockito.verify(viewState, Mockito.times(applyViewState))
          .apply(Mockito.eq(view), Mockito.eq(viewsStateRestoredFromMemory));
    }

    Mockito.verify(callback, Mockito.times(setRestoreViewState)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(setRestoreViewState)).setRestoringViewState(false);

    Mockito.verify(callback, Mockito.times(onNewViewStateInstance)).onNewViewStateInstance();

    if (viewsStateRestoredFromMemory == null) {
      Mockito.verify(callback, Mockito.times(0)).onViewStateInstanceRestored(true);
      Mockito.verify(callback, Mockito.times(0)).onViewStateInstanceRestored(false);
    } else {
      Mockito.verify(callback, Mockito.times(onViewStateInstanceRestored))
          .onViewStateInstanceRestored(viewsStateRestoredFromMemory);
    }
  }

  private void finishFragment(
      FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate,
      Bundle bundle) {
    delegate.onPause();
    delegate.onSaveInstanceState(bundle);
    delegate.onStop();
    delegate.onDestroyView();
    delegate.onDestroy();
    delegate.onDetach();
  }

  private void finishFragment(Bundle bundle, int detachViewCount, int destroyPresenterCount,
      boolean changingConfigurations, boolean isFinishing) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(changingConfigurations);
    Mockito.when(activity.isFinishing()).thenReturn(isFinishing);
    finishFragment(delegate, bundle);
    Mockito.verify(presenter, Mockito.times(detachViewCount)).detachView();
    Mockito.verify(presenter, Mockito.times(destroyPresenterCount)).destroy();
  }
}
