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

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

/**
 * @author Hannes Dorfmann
 */
public class ActivityMvpViewStateDelegateImplTestNew {

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private PartialMvpViewStateDelegateCallbackImpl callback;
  private Activity activity;
  private Application application;
  private ViewState<MvpView> viewState;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    viewState = Mockito.mock(ViewState.class);

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.spy(PartialMvpViewStateDelegateCallbackImpl.class);
    activity = Mockito.mock(Activity.class);
    application = Mockito.mock(Application.class);

    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.doCallRealMethod().when(callback).getViewState();

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(activity.getApplication()).thenReturn(application);

    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {

    ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate =
        new ActivityMvpViewStateDelegateImpl<>(activity, callback, true);

    startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishActivity(delegate, bundle, true, 1, true, false);
    startActivity(delegate, bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
    finishActivity(delegate, bundle, false, 1, false, true);
  }

  @Test public void appStartFinishing() {
    ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate =
        new ActivityMvpViewStateDelegateImpl<>(activity, callback, true);

    startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishActivity(delegate, bundle, false, 1, false, true);
  }

  @Test public void dontKeepPresenterAndViewState() {
    ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate =
        new ActivityMvpViewStateDelegateImpl<>(activity, callback, false);
    startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishActivity(delegate, bundle, false, 1, true, false);
    startActivity(delegate, bundle, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
    finishActivity(delegate, bundle, false, 2, false, true);
  }

  @Test public void appStartAfterProcessDeathAndViewStateRecreationFromBundle() {
    ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate =
        new ActivityMvpViewStateDelegateImpl<>(activity, callback, true);

    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        viewState = Mockito.spy(new SimpleRestorableViewState());
        return viewState;
      }
    }).when(callback).createViewState();

    Bundle bundle = BundleMocker.create();
    bundle.putString(ActivityMvpViewStateDelegateImpl.KEY_MOSBY_VIEW_ID, "123456789");

    startActivity(delegate, bundle, 1, 1, 1, 1, 1, 1, false, 1, 0, 1);
  }

  @Test public void appStartWithViewStateFromMemoryAndBundleButPreferViewStateFromMemory() {

    ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate =
        new ActivityMvpViewStateDelegateImpl<>(activity, callback, true);

    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        viewState = Mockito.spy(new SimpleRestorableViewState());
        return viewState;
      }
    }).when(callback).createViewState();

    startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    Bundle bundle = BundleMocker.create();
    finishActivity(delegate, bundle, true, 1, true, false);
    startActivity(delegate, bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
    finishActivity(delegate, bundle, false, 1, false, true);
  }

  private void startActivity(
      ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate,
      Bundle bundle, int createPresenter, int setPresenter, int attachView, int createViewState,
      int setViewState, int applyViewState, Boolean viewsStateRestoredFromMemory,
      int setRestoreViewState, int onNewViewStateInstance, int onViewStateInstanceRestored) {

    delegate.onCreate(bundle);
    delegate.onContentChanged();
    delegate.onPostCreate(bundle);
    delegate.onStart();
    delegate.onResume();

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

  private void finishActivity(ActivityMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate,
      Bundle bundle, boolean expectKeepPresenter, int detachViewCount,
      boolean changingConfigurations, boolean isFinishing) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(changingConfigurations);
    Mockito.when(activity.isFinishing()).thenReturn(isFinishing);

    delegate.onPause();
    delegate.onSaveInstanceState(bundle);
    delegate.onStop();
    delegate.onDestroy();
    delegate.onRestart();

    Mockito.verify(presenter, Mockito.times(detachViewCount)).detachView(expectKeepPresenter);
  }
}
