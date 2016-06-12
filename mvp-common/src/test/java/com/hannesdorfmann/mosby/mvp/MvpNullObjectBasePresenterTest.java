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

import com.hannesdorfmann.mosby.mvp.test.data.TestData;
import com.hannesdorfmann.mosby.mvp.test.presenter.ParameterlessConstructorMvpPresenter;
import com.hannesdorfmann.mosby.mvp.test.presenter.SubMvpPresenter;
import com.hannesdorfmann.mosby.mvp.test.presenter.SubParameterlessConstructorMvpPresenter;
import com.hannesdorfmann.mosby.mvp.test.presenter.UselessGenericParamsMvpPresenter;
import com.hannesdorfmann.mosby.mvp.test.view.SubMvpView;
import com.hannesdorfmann.mosby.mvp.test.view.TestMvpView;
import com.hannesdorfmann.mosby.mvp.test.view.TestMvpViewWithMultipleInterfaces;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Hannes Dorfmann
 *         <p/>
 *         In all test we are not expecting any exceptions
 */
public class MvpNullObjectBasePresenterTest {

  @Test
  @SuppressWarnings("unchecked")
  public void testUselessGenericsParamsPresenter() {
    TestMvpView view = newTestView();
    UselessGenericParamsMvpPresenter presenter = new UselessGenericParamsMvpPresenter<>();

    testPickingCorrectViewInterface(presenter);
    testAttachDetachView(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }

  @Test
  public void testConstructorGenericParameterless() {
    ParameterlessConstructorMvpPresenter<TestData> presenter = new ParameterlessConstructorMvpPresenter<>();
    TestMvpView view = newTestView();

    testPickingCorrectViewInterface(presenter);
    testAttachDetachView(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }

  @Test
  public void testConstructorDirectlyBaseClass() {
    MvpNullObjectBasePresenter<TestMvpView> presenter = new MvpNullObjectBasePresenter<TestMvpView>() {
    };
    TestMvpView view = newTestView();

    testPickingCorrectViewInterface(presenter);
    testAttachDetachView(presenter, view);
  }

  @Test
  public void testConstructorSubClass() {
    SubParameterlessConstructorMvpPresenter presenter = new SubParameterlessConstructorMvpPresenter();
    TestMvpView view = newTestView();

    testPickingCorrectViewInterface(presenter);
    testAttachDetachView(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }

  @Test
  public void testSubviewInterface() {
    SubMvpPresenter presenter = new SubMvpPresenter();
    SubMvpView view = newSubTestView();

    testAttachDetachView(presenter, view);

    presenter.attachView(view);
    presenter.invokeShowThat();
    presenter.detachView(false);
    presenter.invokeShowThat();
  }

  private <V extends MvpView> void testAttachDetachView(final MvpNullObjectBasePresenter<V> presenter,
                                                        final V view) {
    assertNotNull(presenter.getView());

    testAttachView(presenter, view);
    testDetachNonRetain(presenter, view);
    testAttachView(presenter, view);
    testDetachRetain(presenter, view);
  }

  private <V extends MvpView> void testAttachView(final MvpNullObjectBasePresenter<V> presenter,
                                                  final V view) {
    presenter.attachView(view);
    assertNotNull(presenter.getView());
    assertTrue(presenter.getView() == view);
  }

  private <V extends MvpView> void testDetachNonRetain(final MvpNullObjectBasePresenter<V> presenter,
                                                       final V view) {
    presenter.detachView(false);
    assertNotNull(presenter.getView());
    assertTrue(presenter.getView() != view); // Null Object view;
  }

  private <V extends MvpView> void testDetachRetain(final MvpNullObjectBasePresenter<V> presenter,
                                                    final V view) {
    presenter.detachView(true);
    assertNotNull(presenter.getView());
    assertTrue(presenter.getView() != view); // Null Object view;
  }

  private void testPickingCorrectViewInterface(final MvpNullObjectBasePresenter<TestMvpView> presenter) {
    TestMvpViewWithMultipleInterfaces view = new TestMvpViewWithMultipleInterfaces();

    presenter.attachView(view);
    assertNotNull(presenter.getView());
    assertTrue(view == presenter.getView());

    presenter.detachView(false);
    assertNotNull(presenter.getView());
    Assert.assertFalse(presenter.getView() == view);

    // Invoke methods on null object
    presenter.getView().showFoo(new TestData());
    presenter.getView().showThat();
  }

  private TestMvpView newTestView() {
    return new TestMvpView() {
      @Override
      public void showFoo(TestData data) {
      }

      @Override
      public void showThat() {
      }
    };
  }

  private SubMvpView newSubTestView() {
    return new SubMvpView() {
      @Override
      public void showFoo(TestData data) {
      }

      @Override
      public void showThat() {

      }
    };
  }
}
