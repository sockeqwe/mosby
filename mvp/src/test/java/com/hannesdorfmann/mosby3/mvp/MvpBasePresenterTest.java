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

package com.hannesdorfmann.mosby3.mvp;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Hannes Dorfmann
 */
public class MvpBasePresenterTest {

  @Test public void testAttachView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    Assert.assertFalse(presenter.isViewAttached());
    presenter.attachView(mvpView);
    Assert.assertTrue(presenter.isViewAttached());
  }

  @Test public void testDetachView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    presenter.attachView(mvpView);
    presenter.detachView(false);
    assertViewNotAttachedAndNull(presenter);
  }

  @Test public void testGetView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    Assert.assertNull(presenter.getView());
    presenter.attachView(mvpView);
    Assert.assertNotNull(presenter.getView());
  }

  @Test public void testOnDestroy() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView view = new MvpView() {
    };

    assertViewNotAttachedAndNull(presenter);
    presenter.attachView(view);

    assertViewAttachedAndNotNull(presenter);
    assertEquals(presenter.getView(), view);

    presenter.detachView(true);
    assertViewNotAttachedAndNull(presenter);

    presenter.attachView(view);
    presenter.detachView(false);
    assertViewNotAttachedAndNull(presenter);
  }

  private void assertViewAttachedAndNotNull(final MvpBasePresenter presenter) {
    Assert.assertTrue(presenter.isViewAttached());
    Assert.assertNotNull(presenter.getView());
  }

  private void assertViewNotAttachedAndNull(final MvpBasePresenter presenter) {
    Assert.assertFalse(presenter.isViewAttached());
    Assert.assertNull(presenter.getView());
  }
}
