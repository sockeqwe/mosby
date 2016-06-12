package com.hannesdorfmann.mosby.mvp.test.view;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.test.data.TestData;

public interface TestMvpView extends MvpView {

  void showFoo(TestData data);

  void showThat();
}
