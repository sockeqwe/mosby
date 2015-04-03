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

package com.hannesdorfmann.mosby.testing.presenter;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * A simple MVP LCE (Loading Content Error) Unit test for Presenter to ensure that showLoading(),
 * showContent()
 * and setData() will be called correctly as expected
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class MvpLcePresenterTest<M, V extends MvpLceView<M>, P extends MvpPresenter<V>>  {


  /**
   * Called to indicate that we are testing the successful way. So we expect that the presenter calls
   * showContent() and setData(). You may have to manipulate your business logic objects (like http component) to
   * ensure that NO ERROR is thrown.
   */
  protected abstract void beforeTestNotFailing();

  /**
   * Called to indicate that we are testing errors. So we expect that the presenter calls
   * showError(). You may have to manipulate your business logic objects (like http component) to
   * ensure an error is thrown that is handled by the presenter properly
   */
  protected abstract void beforeTestFailing();

  /**
   * Starts the unit unit test by checking if showError(), showContent() and showLoading() is
   * invoked correctly and that the expected data of setData() is as expected.
   *
   * @param pullToRefreshSupported Does this presenter supports pull to refresh. If true, pull to
   * refresh will also be tested
   */
  protected void startLceTests(boolean pullToRefreshSupported) {

    P presenter = freshPresenter();

    //
    // Load successfully , not pull to refresh
    //
    beforeTestNotFailing();
    V view = createViewMock();
    presenter.attachView(view);
    loadData(presenter, false);
    verify(view, never()).showError(any(Exception.class), anyBoolean());
    verify(view, times(1)).showLoading(false);
    verify(view, times(1)).showContent();
    verify(view, times(1)).setData((M) Matchers.anyObject());
    verifyNoMoreInteractions(view);

    if (pullToRefreshSupported) {
      //
      // Load successfully, pull to refresh
      //
      view = createViewMock();
      presenter.attachView(view);
      loadData(presenter, true);
      verify(view, never()).showError(any(Exception.class), anyBoolean());
      verify(view, times(1)).showLoading(true);
      verify(view, times(1)).showContent();
      verify(view, times(1)).setData((M) Matchers.anyObject());
      verifyNoMoreInteractions(view);
    }

    //
    // Error while loading, not pull to refresh
    //
    beforeTestFailing();
    view = createViewMock();
    presenter = freshPresenter();
    presenter.attachView(view);
    loadData(presenter, false);
    verify(view, times(1)).showLoading(false);
    verify(view, times(1)).showError(any(Exception.class), Matchers.eq(false));
    verify(view, never()).showContent();
    verify(view, never()).setData((M) Matchers.anyObject());
    verifyNoMoreInteractions(view);

    if (pullToRefreshSupported) {
      view = createViewMock();
      presenter.attachView(view);
      loadData(presenter, true);
      verify(view, times(1)).showLoading(true);
      verify(view, times(1)).showError(any(Exception.class), Matchers.eq(true));
      verify(view, never()).showContent();
      verify(view, never()).setData((M) Matchers.anyObject());
      verifyNoMoreInteractions(view);
    }
  }

  /**
   * Create a new view mock instance
   */
  protected V createViewMock() {
    V view = Mockito.mock(getViewClass());
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        M data = (M) invocation.getArguments()[0];
        verifyData(data);
        return null;
      }
    }).when(view).setData((M) Matchers.anyObject());

    return view;
  }

  protected P freshPresenter() {
    return createPresenter();
  }

  /**
   * Get the class of the view (the view interface)
   */
  protected abstract Class<V> getViewClass();

  /**
   * Get the presenter instance
   */
  protected abstract P createPresenter();

  /**
   * Invoked to load data
   *
   * @param presenter the presenter where you should call the loading method
   */
  protected abstract void loadData(P presenter, boolean pullToRefresh);

  /**
   * Called to verify that data that has been set to the view by {@link MvpLceView#setData(Object)}
   *
   * @param data The data
   */
  protected abstract void verifyData(M data);
}
