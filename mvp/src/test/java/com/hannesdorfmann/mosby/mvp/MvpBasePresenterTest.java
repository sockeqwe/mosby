package com.hannesdorfmann.mosby.mvp;

import junit.framework.Assert;
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

    setView(view);

    Assert.assertTrue(isViewAttached());
    Assert.assertNotNull(getView());
    Assert.assertEquals(getView(), view);

    onDestroy(true);
    noViewAttached();


    setView(view);
    onDestroy(false);
    noViewAttached();


  }


  private void noViewAttached(){
    Assert.assertFalse(isViewAttached());
  }
}
