package com.hannesdorfmann.mosby3.mvp;

import android.support.annotation.NonNull;

/**
 * @author Hannes Dorfmann
 */

public class TestPresenter extends MvpQueuingBasePresenter<TestView>{

  public void triggerViewShow(final int i){

    onceViewAttached(new ViewAction<TestView>() {
      @Override public void run(@NonNull TestView view) {
        view.show(i);
      }
    });

  }
}
