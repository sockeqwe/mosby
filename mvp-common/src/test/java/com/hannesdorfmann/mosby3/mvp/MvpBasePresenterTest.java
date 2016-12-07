package com.hannesdorfmann.mosby3.mvp;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Hannes Dorfmann
 */
public class MvpBasePresenterTest {

  @Test
  public void testAttachView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    assertFalse(presenter.isViewAttached());
    presenter.attachView(mvpView);
    assertTrue(presenter.isViewAttached());
  }

  @Test
  public void testDetachView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    presenter.attachView(mvpView);
    presenter.detachView(false);
    assertViewNotAttachedAndNull(presenter);
  }

  @Test
  public void testGetView() {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };

    assertNull(presenter.getView());
    presenter.attachView(mvpView);
    assertNotNull(presenter.getView());
  }

  @Test
  public void testOnDestroy() {
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
    assertTrue(presenter.isViewAttached());
    assertNotNull(presenter.getView());
  }

  private void assertViewNotAttachedAndNull(final MvpBasePresenter presenter) {
    assertFalse(presenter.isViewAttached());
    assertNull(presenter.getView());
  }
}
