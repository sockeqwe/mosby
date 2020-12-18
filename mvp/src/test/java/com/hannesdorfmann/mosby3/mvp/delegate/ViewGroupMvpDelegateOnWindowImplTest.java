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
import android.view.View;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ViewGroupMvpDelegateOnWindowImplTest {

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private PartialViewGroupMvpDelegateCallbackImpl callback;
  private ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate;
  private Application application;
  private View androidView;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialViewGroupMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).superOnSaveInstanceState();
    Mockito.when(callback.isViewOnActivity()).thenReturn(false);

    application = Mockito.mock(Application.class);
    androidView = Mockito.mock(View.class);

    Mockito.when(callback.getMvpView()).thenReturn(view);
    Mockito.when(callback.getContext()).thenReturn(application);
    Mockito.when(androidView.isInEditMode()).thenReturn(false);

    delegate = new ViewGroupMvpDelegateImpl<>(androidView, callback, true);
  }

  @Test public void appStartWithDoesNotSavePresenter() {
    startViewGroup(1, 1, 1);
    finishViewGroup(1);
    delegate = new ViewGroupMvpDelegateImpl<>(androidView, callback, true);
    startViewGroup(2, 2, 2);
    finishViewGroup(2);
  }

  @Test public void appStartFinishing() {
    startViewGroup(1, 1, 1);
    finishViewGroup(1);
  }

  @Test public void dontKeepPresenter() {
    delegate =
        new ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>>(androidView, callback, false);
    startViewGroup(1, 1, 1);
    finishViewGroup(1);
    delegate =
        new ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>>(androidView, callback, false);
    startViewGroup(2, 2, 2);
    finishViewGroup(2);
  }

  private void startViewGroup(int createPresenter, int setPresenter, int attachView) {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.times(createPresenter)).createPresenter();
    Mockito.verify(callback, Mockito.times(setPresenter)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(attachView)).attachView(view);
  }

  private void finishViewGroup(int destroyCount) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);

    delegate.onDetachedFromWindow();
    Mockito.verify(presenter, Mockito.times(destroyCount)).destroy();
  }
}
