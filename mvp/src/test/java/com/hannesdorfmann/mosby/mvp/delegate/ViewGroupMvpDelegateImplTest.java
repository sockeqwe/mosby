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

package com.hannesdorfmann.mosby.mvp.delegate;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class ViewGroupMvpDelegateImplTest {

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private BaseMvpDelegateCallback<MvpView, MvpPresenter<MvpView>> callback;
  private ViewGroupMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();

    Mockito.when(callback.getMvpView()).thenReturn(view);

    delegate = new ViewGroupMvpDelegateImpl<>(callback);
  }

  @Test public void startViewGroup() {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);

    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
  }

  @Test public void finishViewGroupNotRetaining() {
    testFinishViewGroup(false);
  }

  @Test public void finishViewGroupIsRetaining() {
    testFinishViewGroup(true);
  }

  private void testFinishViewGroup(boolean retainingInstanceState) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retainingInstanceState);

    delegate.onDetachedFromWindow();

    Mockito.verify(presenter, Mockito.times(1)).detachView(retainingInstanceState);
  }

  @Test
  public void reuseRetainingPresenter(){
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
  }
}
