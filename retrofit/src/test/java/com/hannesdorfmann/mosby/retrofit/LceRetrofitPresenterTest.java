package com.hannesdorfmann.mosby.retrofit;

import com.hannesdorfmann.mosby.retrofit.exception.NetworkException;
import com.hannesdorfmann.mosby.retrofit.exception.UnexpectedStatusCodeException;
import com.hannesdorfmann.mosby.retrofit.mock.MockTestApi;
import com.hannesdorfmann.mosby.testing.presenter.MvpLcePresenterTest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class LceRetrofitPresenterTest extends MvpLcePresenterTest<Object, TestView, TestPresenter> {

  private MockTestApi testApi = new MockTestApi();
  private TestPresenter presenter = new TestPresenter(testApi);
  private Object data = new Object();

  @Override protected void beforeTestNotFailing() {
    testApi.setShouldFail(false);
    testApi.setData(data);
  }

  @Override protected void beforeTestFailing() {
    testApi.setShouldFail(true);
    testApi.setReason(MockTestApi.FailReason.UNKNOWN);
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

  @Test public void testPresenter() {
    super.startLceTests(true);
  }

  @Test public void testNetworkException() {
    testApi.setShouldFail(true);
    testApi.setReason(MockTestApi.FailReason.NETWORK);

    TestView view = new TestView() {

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.assertTrue(e instanceof NetworkException);
      }

      @Override public void showLoading(boolean pullToRefresh) {
        // Not to check
      }

      @Override public void showContent() {
        Assert.fail("showContent() called, but should not have been called");
      }

      @Override public void setData(Object data) {
        Assert.fail("setData() called, but should not have been called");
      }

      @Override public void loadData(boolean pullToRefresh) {

      }
    };

    presenter.attachView(view);
    presenter.loadData(false);
    presenter.loadData(true);
  }

  @Test public void testStatusCodeException() {
    testApi.setShouldFail(true);
    testApi.setReason(MockTestApi.FailReason.STATUS_CODE);

    TestView view = new TestView() {

      @Override public void showError(Throwable e, boolean pullToRefresh) {
        Assert.assertTrue(e instanceof UnexpectedStatusCodeException);
        Assert.assertEquals(((UnexpectedStatusCodeException) e).getStatusCode(),
            MockTestApi.FailReason.httpcode);
      }

      @Override public void showLoading(boolean pullToRefresh) {
        // Not to check
      }

      @Override public void showContent() {
        Assert.fail("showContent() called, but should not have been called");
      }

      @Override public void setData(Object data) {
        Assert.fail("setData() called, but should not have been called");
      }

      @Override public void loadData(boolean pullToRefresh) {

      }
    };

    presenter.attachView(view);
    presenter.loadData(false);
    presenter.loadData(true);
  }
}
