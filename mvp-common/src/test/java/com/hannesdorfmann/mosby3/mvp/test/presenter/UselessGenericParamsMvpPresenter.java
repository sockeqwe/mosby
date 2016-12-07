package com.hannesdorfmann.mosby3.mvp.test.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby3.mvp.test.view.TestMvpView;

public class UselessGenericParamsMvpPresenter<M, I>
        extends MvpNullObjectBasePresenter<TestMvpView> {

  public void viewShowThat() {
    getView().showThat();
  }
}
