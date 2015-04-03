package com.hannesdorfmann.mosby.rx.lce;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import rx.Observable;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * @author Hannes Dorfmann
 */
public class TestPresenter extends MvpLceRxPresenter<TestView, Object> {

  private Object data;
  private boolean fail;

  public void setData(Object data) {
    this.data = data;
  }

  public void setFail(boolean fail) {
    this.fail = fail;
  }

  public void loadData(boolean pullToRefresh) {
    Observable<Object> observable;

    if (fail) {
      observable = Observable.error(new Exception("Mock Exception"));
    } else {

      observable = Observable.defer(new Func0<Observable<Object>>() {
        @Override public Observable<Object> call() {
          return Observable.just(data);
        }
      });
    }

    subscribe(observable, pullToRefresh);
  }

  @Override protected Observable<Object> applyScheduler(Observable<Object> observable) {
    return observable.subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate());
  }
}
