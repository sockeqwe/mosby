package com.hannesdorfmann.mosby.mvp;

import org.junit.Assert;
import org.junit.Test;

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
}
