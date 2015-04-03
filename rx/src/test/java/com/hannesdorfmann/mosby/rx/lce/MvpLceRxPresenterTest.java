package com.hannesdorfmann.mosby.rx.lce;

import com.hannesdorfmann.mosby.testing.presenter.MvpLcePresenterTest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class MvpLceRxPresenterTest extends MvpLcePresenterTest<Object, TestView, TestPresenter> {

  private TestPresenter presenter = new TestPresenter();
  private Object data = new Object();

  @Override protected void beforeTestNotFailing() {
    presenter.setFail(false);
    presenter.setData(data);
  }

  @Override protected void beforeTestFailing() {
    presenter.setData(null);
    presenter.setFail(true);
  }

  @Override protected Class<TestView> getViewClass() {
    return TestView.class;
  }

  @Override protected TestPresenter createPresenter() {
    return presenter;
  }

  @Override protected void loadData(TestPresenter presenter, boolean pullToRefresh) {
    presenter.loadData(pullToRefresh);
  }

  @Override protected void verifyData(Object data) {
    Assert.assertTrue(data == this.data);
  }

  @Test
  public void testPresenter(){
    super.startLceTests(true);
  }
}
