package com.hannesdorfmann.mosby3.mvp.test.view;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.test.data.TestData;

public interface TestMvpView extends MvpView {

  void showFoo(TestData data);

  void showThat();
}
