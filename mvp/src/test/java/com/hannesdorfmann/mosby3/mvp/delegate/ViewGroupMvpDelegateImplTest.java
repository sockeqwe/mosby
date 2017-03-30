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
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Hannes Dorfmann
 */
public class ViewGroupMvpDelegateImplTest {

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private PartialViewGroupMvpDelegateCallbackImpl callback;
  private ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate;
  private FragmentActivity activity;
  private Application application;
  private View androidView;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialViewGroupMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();

    activity = Mockito.mock(FragmentActivity.class);
    application = Mockito.mock(Application.class);
    androidView = Mockito.mock(View.class);

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(callback.getContext()).thenReturn(activity);
    Mockito.when(activity.getApplication()).thenReturn(application);
    Mockito.when(androidView.isInEditMode()).thenReturn(false);

    delegate = new ViewGroupMvpDelegateImpl<>(androidView, callback, true);
  }

  @Test public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
    startViewGroup(1, 1, 1);
    finishViewGroup(1, true, true, false);
    startViewGroup(1, 2, 2);
    finishViewGroup(1, false, false, true);
  }

  @Test public void appStartFinishing() {
    startViewGroup(1, 1, 1);
    finishViewGroup(1, false, false, true);
  }

  @Test public void dontKeepPresenter() {
    delegate = new ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>>(androidView, callback, false);
    startViewGroup(1, 1, 1);
    finishViewGroup(1, false, true, false);
    startViewGroup(2, 2, 2);
    finishViewGroup(2, false, false, true);
  }


  /**
   * Checks if two Views one that keeps presenter, the other who doesn't keep presenter during
   * screen orientation changes work properly
   *
   * https://github.com/sockeqwe/mosby/issues/231
   */
  @Test public void dontKeepPresenterIfSecondPresenterInPresenterManager() {

    MvpView view1 = new MvpView() {
    };

    MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
    PartialViewGroupMvpDelegateCallbackImpl callback1 = Mockito.mock(PartialViewGroupMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
    Mockito.doCallRealMethod().when(callback1).getPresenter();
    View androidView1 = Mockito.mock(View.class);

    Mockito.when(callback1.getMvpView()).thenReturn(view1);
    Mockito.when(callback1.getContext()).thenReturn(activity);
    Mockito.when(androidView1.isInEditMode()).thenReturn(false);
    Mockito.when(callback1.createPresenter()).thenReturn(presenter1);

    ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> keepDeelgate
        = new ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>>(androidView1, callback1, true);


    delegate = new ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>>(androidView, callback, false);

    keepDeelgate.onAttachedToWindow();

    startViewGroup(1, 1, 1);
    finishViewGroup(1, false, true, false);
    startViewGroup(2, 2, 2);
    finishViewGroup(2, false, false, true);

    keepDeelgate.onDetachedFromWindow();
  }


  private void startViewGroup(int createPresenter, int setPresenter, int attachView) {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);

    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.times(createPresenter)).createPresenter();
    Mockito.verify(callback, Mockito.times(setPresenter)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(attachView)).attachView(view);
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
