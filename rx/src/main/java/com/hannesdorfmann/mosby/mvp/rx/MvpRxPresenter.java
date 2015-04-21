package com.hannesdorfmann.mosby.mvp.rx;


import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.rx.scheduler.AndroidSchedulerTransformer;

import rx.Observable;
import rx.Subscriber;

/**
 * A presenter for RxJava, that assumes that only one Observable is subscribed by this presenter.
 * The idea is, that you make your (chain of) Observable and pass it to {@link
 * #subscribe(Observable)}. The presenter internally subscribes himself as Subscriber to the
 * observable
 * (which executes the observable). Before subscribing the presenter calls
 * {@link #applyScheduler(Observable)} to apply the <code>subscribeOn()</code> and
 * <code>observeOn()</code>
 *
 * @author Fahim Karim
 * @since 1.0.0
 */
public abstract class MvpRxPresenter<V extends MvpView, M>
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
     */
    public void subscribe(Observable<M> observable) {

        onRxSubscribe();

        unsubscribe();

        subscriber = new Subscriber<M>() {
            @Override public void onCompleted() {
                MvpRxPresenter.this.onCompleted();
            }

            @Override public void onError(Throwable e) {
                MvpRxPresenter.this.onError(e);
            }

            @Override public void onNext(M m) {
                MvpRxPresenter.this.onNext(m);
            }
        };

        observable = applyScheduler(observable);
        observable.subscribe(subscriber);
    }

    /**
     * Called in {@link #subscribe(Observable)} to set  <code>subscribeOn()</code> and
     * <code>observeOn()</code>. As default it uses {@link AndroidSchedulerTransformer}. Override
     * this
     * method if you want to provide your own scheduling implementation.
     */
    protected Observable<M> applyScheduler(Observable<M> observable) {
        return observable.compose(new AndroidSchedulerTransformer<M>());
    }

    protected void onCompleted() {
        onRxCompleted();
        unsubscribe();
    }

    protected void onError(Throwable e) {
        onRxError(e);
        unsubscribe();
    }

    protected void onNext(M data) {
        onRxNext(data);
    }

    public abstract void onRxSubscribe();
    public abstract void onRxNext(M data);
    public abstract void onRxError(Throwable e);
    public abstract void onRxCompleted();

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            unsubscribe();
        }
    }
}
