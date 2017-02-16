package com.hannesdorfmann.mosby3.mvp.test.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby3.mvp.test.view.SubMvpView;

public class SubMvpPresenter
        extends MvpNullObjectBasePresenter<SubMvpView> {

  public void invokeShowThat() {
    getView().showThat();
  }
}
