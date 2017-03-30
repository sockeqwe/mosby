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
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class ViewGroupMvpViewStateDelegateImplTest {

  private MvpView view;
  private ViewState<MvpView> viewState;
  private MvpPresenter<MvpView> presenter;
  private PartialViewGroupMvpViewStateDelegateCallbackImpl callback;
  private ViewGroupMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>>
      delegate;
  private FragmentActivity activity;
  private Application application;
  private View androidView;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    viewState = Mockito.mock(ViewState.class);

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialViewGroupMvpViewStateDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.doCallRealMethod().when(callback).getViewState();

    activity = Mockito.mock(FragmentActivity.class);
    application = Mockito.mock(Application.class);
    androidView = Mockito.mock(View.class);

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(callback.getContext()).thenReturn(activity);
    Mockito.when(activity.getApplication()).thenReturn(application);

    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);
    Mockito.when(androidView.isInEditMode()).thenReturn(false);

    delegate = new ViewGroupMvpViewStateDelegateImpl<>(androidView, callback, true);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
    startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    finishViewGroup(1, true, true, false);
    startViewGroup(1, 2, 2, 1, 2, 1, true, 1, 1, 1);
    finishViewGroup(1, false, false, true);
  }

  @Test public void appStartFinishing() {
    startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    finishViewGroup(1, false, false, true);
  }

  @Test public void dontKeepPresenter() {
    delegate = new ViewGroupMvpViewStateDelegateImpl<>(androidView, callback, false);
    startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    finishViewGroup(1, false, true, false);
    startViewGroup(2, 2, 2, 2, 2, 0, null, 0, 2, 0);
    finishViewGroup(2, false, false, true);
  }

  /**
   * Checks if two Views one that keeps presenter, the other who doesn't keep presenter during
   * screen orientation changes work properly
   *
   * https://github.com/sockeqwe/mosby/issues/231
   */
  @Test public void dontKeepPresenterIfSecondPresenterIsInPresenterManager() {

    MvpView view1 = new MvpView() {
    };

    View androidView1 = Mockito.mock(View.class);
    ViewState<MvpView> viewState1 = Mockito.mock(ViewState.class);
    MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
    PartialViewGroupMvpViewStateDelegateCallbackImpl callback1 =
        Mockito.mock(PartialViewGroupMvpViewStateDelegateCallbackImpl.class);

    Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
    Mockito.doCallRealMethod().when(callback1).getPresenter();
    Mockito.doCallRealMethod().when(callback1).setViewState(viewState1);
    Mockito.doCallRealMethod().when(callback1).getViewState();

    Mockito.when(callback1.getMvpView()).thenReturn(view1);
    Mockito.when(callback1.getContext()).thenReturn(activity);

    Mockito.when(callback1.createPresenter()).thenReturn(presenter1);
    Mockito.when(callback1.createViewState()).thenReturn(viewState1);
    Mockito.when(androidView1.isInEditMode()).thenReturn(false);

    ViewGroupMvpViewStateDelegateImpl keepDelegate =
        new ViewGroupMvpViewStateDelegateImpl<>(androidView1, callback1, true);

    keepDelegate.onAttachedToWindow();

    delegate = new ViewGroupMvpViewStateDelegateImpl<>(androidView, callback, false);
    startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
    finishViewGroup(1, false, true, false);
    startViewGroup(2, 2, 2, 2, 2, 0, null, 0, 2, 0);
    finishViewGroup(2, false, false, true);

    keepDelegate.onDetachedFromWindow();
  }

  @Test public void appStartWithProcessDeathAndViewStateRecreationFromBundle() {
    //    Assert.fail("Not implemented");
    // TODO implement
  }

  @Test public void appStartWithViewStateFromMemoryAndBundleButPreferViewStateFromMemory() {
    //    Assert.fail("Not implemented");
    // TODO implement
  }

  private void startViewGroup(int createPresenter, int setPresenter, int attachView,
      int createViewState, int setViewState, int applyViewState,
      Boolean viewsStateRestoredFromMemory, int setRestoreViewState, int onNewViewStateInstance,
      int onViewStateInstanceRestored) {

    delegate.onAttachedToWindow();

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

  private void finishViewGroup(int detachCount, boolean expectKeepPresenter, boolean configChange,
      boolean finishingActivity) {

    Mockito.when(activity.isFinishing()).thenReturn(finishingActivity);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(configChange);
    Mockito.when(callback.getPresenter()).thenReturn(presenter);

    delegate.onDetachedFromWindow();

    Mockito.verify(presenter, Mockito.times(detachCount)).detachView(expectKeepPresenter);
  }
}
