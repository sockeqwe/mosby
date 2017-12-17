package com.hannesdorfmann.mosby3.mvp;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class MvpQueuingBasePresenterTest {
  @Test public void queueActionsAndExecuteOnceViewAttached() throws Exception {

    TestView view = new TestView();
    TestPresenter presenter = new TestPresenter();

    presenter.attachView(view);
    presenter.triggerViewShow(1);
    presenter.triggerViewShow(2);

    Assert.assertEquals(Arrays.asList(1, 2), view.showInvocations);

    presenter.detachView();

    presenter.triggerViewShow(3);
    presenter.triggerViewShow(4);


    Assert.assertEquals(Arrays.asList(1, 2), view.showInvocations);
    presenter.attachView(view);
    Assert.assertEquals(Arrays.asList(1, 2,3,4), view.showInvocations);

    presenter.triggerViewShow(5);
    Assert.assertEquals(Arrays.asList(1, 2,3,4,5), view.showInvocations);
    presenter.detachView();
    presenter.triggerViewShow(6);

    Assert.assertEquals(Arrays.asList(1, 2,3,4,5), view.showInvocations);
  }
}