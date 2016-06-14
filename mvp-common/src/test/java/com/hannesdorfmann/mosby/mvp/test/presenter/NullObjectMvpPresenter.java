package com.hannesdorfmann.mosby.mvp.test.presenter;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.test.data.TestData;
import com.hannesdorfmann.mosby.mvp.test.view.TestMvpView;

public class NullObjectMvpPresenter
        extends MvpNullObjectBasePresenter<TestMvpView> {

  public void viewShowFoo(TestData data) {
    getView().showFoo(data);
  }

  public void viewShowThat() {
    getView().showThat();
  }

  @NonNull
  @Override
  public TestMvpView getView() {
    return super.getView();
  }
}
