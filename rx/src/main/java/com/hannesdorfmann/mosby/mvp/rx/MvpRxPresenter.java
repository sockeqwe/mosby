package com.hannesdorfmann.mosby.mvp.rx;


import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.rx.scheduler.AndroidSchedulerTransformer;

import rx.Observable;
import rx.Subscriber;

/**
 * A presenter for RxJava, that assumes that only one Observable is subscribed by this presenter.
 * The idea is, that you make your (chain of) Observable and pass it to {@link
 * #subscribe(Observable, boolean)}. The presenter internally subscribes himself as Subscriber to the
 * observable
 * (which executes the observable). Before subscribing the presenter calls
 * {@link #applyScheduler(Observable)} to apply the <code>subscribeOn()</code> and
 * <code>observeOn()</code>
 *
 * @author Fahim Karim
 * @since 1.0.0
 */
public abstract class MvpRxPresenter<V extends MvpLceView<M>, M>
        extends com.hannesdorfmann.mosby.mvp.MvpBasePresenter<V>
        implements com.hannesdorfmann.mosby.mvp.MvpPresenter<V> {

    protected Subscriber<M> subscriber;

    /**
     * Unsubscribes the subscriber and set it to null
     */
    protected void unsubscribe() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }

        subscriber = null;
    }

    /**
     * Subscribes the presenter himself as subscriber on the observable
     *
     * @param observable The observable to subscribe
     * @param pullToRefresh Pull to refresh?
     */
    public void subscribe(Observable<M> observable, final boolean pullToRefresh) {

        if (isViewAttached()) {
            getView().showLoading(pullToRefresh);
        }

        unsubscribe();

        subscriber = new Subscriber<M>() {
            private boolean ptr = pullToRefresh;

            @Override public void onCompleted() {
                MvpLceRxPresenter.this.onCompleted();
            }

            @Override public void onError(Throwable e) {
                MvpLceRxPresenter.this.onError(e, ptr);
            }

            @Override public void onNext(M m) {
                MvpLceRxPresenter.this.onNext(m);
            }
        };

        observable = applyScheduler(observable);
        observable.subscribe(subscriber);
    }

    /**
     * Called in {@link #subscribe(Observable, boolean)} to set  <code>subscribeOn()</code> and
     * <code>observeOn()</code>. As default it uses {@link AndroidSchedulerTransformer}. Override
     * this
     * method if you want to provide your own scheduling implementation.
     */
    protected Observable<M> applyScheduler(Observable<M> observable) {
        return observable.compose(new AndroidSchedulerTransformer<M>());
    }

    protected void onCompleted() {
        if (isViewAttached()) {
            getView().showContent();
        }
        unsubscribe();
    }

    protected void onError(Throwable e, boolean pullToRefresh) {
        if (isViewAttached()) {
            getView().showError(e, pullToRefresh);
        }
        unsubscribe();
    }

    protected void onNext(M data) {
        if (isViewAttached()) {
            getView().setData(data);
        }
    }

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            unsubscribe();
        }
    }
}
