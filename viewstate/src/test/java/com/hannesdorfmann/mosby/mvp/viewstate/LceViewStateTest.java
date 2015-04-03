/*
 * Copyright (c) 2015 Hannes Dorfmann.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.mvp.viewstate;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsLceViewState;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class LceViewStateTest {

  @Test
  public void testLceViewState() {

    AbsLceViewState<Object, MvpLceView<Object>> viewState = new AbsLceViewState<Object, MvpLceView<Object>>() {
    };

    // Loading without pull To Refresh
    viewState.setStateShowLoading(false);
    viewState.apply(new MvpLceView<Object>() {
      @Override public void showLoading(boolean pullToRefresh) {
        Assert.assertFalse(pullToRefresh);
      }

      @Override public void showContent() {
        Assert.fail("showContent() called but shouldn't");
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {

        Assert.fail("showError() called but shouldn't");
      }

      @Override public void setData(Object data) {

        Assert.fail("setData() called but shouldn't");
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.fail("loadData() called but shouldn't");
      }
    }, true);

    viewState.setStateShowLoading(false);
    viewState.apply(new MvpLceView<Object>() {
      @Override public void showLoading(boolean pullToRefresh) {
        Assert.assertFalse(pullToRefresh);
      }

      @Override public void showContent() {
        Assert.fail("showContent() called but shouldn't");
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {

        Assert.fail("showError() called but shouldn't");
      }

      @Override public void setData(Object data) {

        Assert.fail("setData() called but shouldn't");
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.assertTrue(true);
      }
    }, false);

    // Content
    final Object content = new Object();
    viewState.setStateShowContent(content);
    viewState.apply(new MvpLceView<Object>() {
      @Override public void showLoading(boolean pullToRefresh) {
        Assert.fail("showLoading() called but shouldn't");
      }

      @Override public void showContent() {
        Assert.assertTrue(true);
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.fail("showError() called but shouldn't");
      }

      @Override public void setData(Object data) {
        Assert.assertTrue(content == data);
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.fail("loadData() called but shouldn't");
      }
    }, false);

    // Loading pull To Refresh (data from previous test)
    viewState.setStateShowContent(content);
    viewState.setStateShowLoading(true);
    viewState.apply(new MvpLceView<Object>() {
      private boolean showContentCalled = false;

      @Override public void showLoading(boolean pullToRefresh) {
        Assert.assertTrue(pullToRefresh);
        Assert.assertTrue(showContentCalled);
      }

      @Override public void showContent() {
        Assert.assertTrue(true);
        showContentCalled = true;
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.fail("showError() called but shouldn't");
      }

      @Override public void setData(Object data) {
        Assert.assertTrue(content == data);
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.assertTrue(true);
      }
    }, false);

    // Error NOT pull to refresh
    final Exception exception = new Exception();
    viewState.setStateShowError(exception, false);
    viewState.apply(new MvpLceView<Object>() {
      @Override public void showLoading(boolean pullToRefresh) {
        Assert.fail("showLoading() called but shouldn't");
      }

      @Override public void showContent() {
        Assert.fail("showContent() called but shouldn't");
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.assertTrue(e == exception);
        Assert.assertFalse(pullToRefresh);
      }

      @Override public void setData(Object data) {
        Assert.fail("setData() called but shouldn't");
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.fail("loadData() called but shouldn't");
      }
    }, false);

    // Error pull to refresh
    viewState.setStateShowContent(content);
    viewState.setStateShowError(exception, true);
    viewState.apply(new MvpLceView<Object>() {
      private boolean showErrorCalled = false;

      @Override public void showLoading(boolean pullToRefresh) {
        Assert.fail("showLoading() called but shouldn't");
      }

      @Override public void showContent() {
        Assert.assertFalse(showErrorCalled);
      }

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.assertTrue(e == exception);
        Assert.assertTrue(pullToRefresh);
        showErrorCalled = true;
      }

      @Override public void setData(Object data) {
        Assert.assertTrue(data == content);
      }

      @Override public void loadData(boolean pullToRefresh) {
        Assert.fail("loadData() called but shouldn't");
      }
    }, false);
  }
}
