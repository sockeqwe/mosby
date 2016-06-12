package com.hannesdorfmann.mosby.mvp.test.presenter;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.test.view.SubMvpView;

public class SubMvpPresenter
        extends MvpNullObjectBasePresenter<SubMvpView> {

  public void invokeShowThat() {
    getView().showThat();
  }
}
