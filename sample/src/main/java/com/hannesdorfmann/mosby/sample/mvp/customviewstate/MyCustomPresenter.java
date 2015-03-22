package com.hannesdorfmann.mosby.sample.mvp.customviewstate;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomPresenter extends MvpBasePresenter<MyCustomView> {

  public void doA() {
    if (isViewAttached()) {
      getView().showA();
    }
  }

  public void doB() {
    if (isViewAttached()) {
      getView().showB();
    }
  }
}
