package com.hannesdorfmann.mosby.mvp.test.presenter;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.test.view.TestMvpView;

public class UselessGenericParamsMvpPresenter<M, I>
        extends MvpNullObjectBasePresenter<TestMvpView> {

  public void viewShowThat() {
    getView().showThat();
  }
}
