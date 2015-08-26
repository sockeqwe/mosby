/*
 *  Copyright 2015. Hannes Dorfmann.
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

package com.hannesdorfmann.mosby.mvp;

import android.support.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class MvpNullObjectBasePresenterTest {

  public interface TestView extends MvpView {
    public void showFoo(TestData data);

    public void showThat();
  }

  /**
   * Just a stupid interface to check if the right interface will be picked
   */
  public interface FooInterface {

    public void foo();
  }

  /**
   * Just a stupid interface to check if the right interface will be picked
   */
  public interface BarInterface {
    public void bar();
  }

  public interface OtherTestView extends MvpView {
    public void showOtherMvpView();
  }

  public static class TestData {

  }

  public static class ViewWithMulitpleInterfaces
      implements FooInterface, BarInterface, OtherTestView, TestView {
    @Override public void bar() {

    }

    @Override public void foo() {

    }

    @Override public void showFoo(TestData data) {

    }

    @Override public void showThat() {

    }

    @Override public void showOtherMvpView() {

    }
  }

  public static class TestNullObjectPresenter
      extends MvpNullObjectBasePresenter<MvpNullObjectBasePresenterTest.TestView> {

    public void viewShowFoo(TestData data) {
      getView().showFoo(data);
    }

    public void viewShowThat() {
      getView().showThat();
    }

    @NonNull @Override public TestView getView() {
      return super.getView();
    }
  }

  @Test public void testAttachDetach() {

    TestNullObjectPresenter presenter = new TestNullObjectPresenter();

    try {
      // NullPointer exception should be thrown
      presenter.getView();
      Assert.fail("Nullpointer Exception should be thrown but haven't");
    } catch (NullPointerException e) {
      // Expected exception
    }

    TestView view = new TestView() {
      @Override public void showFoo(TestData data) {
      }

      @Override public void showThat() {
      }
    };

    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() == view);

    // Test with retainInstance == false
    presenter.detachView(false);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() != view); // Null Object view

    // Reattach real view
    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() == view);

    // Test with retainInstance == true
    presenter.detachView(true);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() != view); // Null Object view
  }

  @Test public void pickingCorrectViewInterface() {

    ViewWithMulitpleInterfaces view = new ViewWithMulitpleInterfaces();
    TestNullObjectPresenter presenter = new TestNullObjectPresenter();

    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(view == presenter.getView());

    presenter.detachView(false);
    Assert.assertNotNull(presenter.getView());
    Assert.assertFalse(presenter.getView() == view);

    // Invoke methods on proxy
    presenter.getView().showFoo(new TestData());
    presenter.getView().showThat();
  }
}
