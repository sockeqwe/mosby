package com.hannesdorfmann.mosby.mvp.rx.lce;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.rx.MvpBaseRxPresenter;
import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.SchedulerTransformer;
import rx.Observable;

/**
 * A presenter for RxJava, that assumes that only one Observable is subscribed by this presenter.
 * The idea is, that you make your (chain of) Observable in {@link #getObservable()}. To execute
 * and
 * subscribe the presenter for this observable you call {@link #subscribe(boolean)}, which will
 * also
 * call {@link #applyScheduler(Observable)} to apply the {@link SchedulerTransformer}
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpLceRxPresenter<V extends MvpLceView<M>, M>
    extends MvpBaseRxPresenter<V, M> {

  private boolean pullToRefresh = false;

  public void subscribe(boolean pullToRefresh) {
    this.pullToRefresh = pullToRefresh;
    Observable<M> observable = getObservable();
    observable = applyScheduler(observable);
    observable.subscribe(this);
  }

  protected Observable<M> applyScheduler(Observable<M> observable) {
    return observable.compose(new AndroidSchedulerTransformer<M>());
  }

  protected abstract Observable<M> getObservable();

  @Override public void onCompleted() {
    if (isViewAttached()) {
      getView().showContent();
    }
  }

  @Override public void onError(Throwable e) {
    if (isViewAttached()) {
      getView().showError(e, pullToRefresh);
    }
  }

  @Override public void onNext(M data) {
    if (isViewAttached()) {
      getView().setData(data);
    }
  }
}
