package com.hannesdorfmann.mosby.mvp;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Hannes Dorfmann
 */
public class MvpBasePresenterTest extends MvpBasePresenter<MvpView> {

  @Test
  public void testOnDestroy(){
    MvpView view = new MvpView() {
    };

    noViewAttached();

    attachView(view);

    Assert.assertTrue(isViewAttached());
    Assert.assertNotNull(getView());
    Assert.assertEquals(getView(), view);

    detachView(true);
    noViewAttached();


    attachView(view);
    detachView(false);
    noViewAttached();

  }


  private void noViewAttached(){
    Assert.assertFalse(isViewAttached());
  }

  @Test
  public void testAttachView() throws Exception {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    assertFalse(presenter.isViewAttached());
    presenter.attachView(new MvpView() {
    });
    assertTrue(presenter.isViewAttached());
  }

  @Test
  public void testGetView() throws Exception {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };
    presenter.attachView(mvpView);
    assertEquals(mvpView, presenter.getView());
  }

  @Test
  public void testDetachView() throws Exception {
    MvpBasePresenter<MvpView> presenter = new MvpBasePresenter<>();
    MvpView mvpView = new MvpView() {
    };
    presenter.attachView(mvpView);
    presenter.detachView(false);
    assertEquals(null, presenter.getView());
  }
}
