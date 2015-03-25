package com.hannesdorfmann.mosby.mvp.rx.lce.scheduler;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import rx.Observable;

/**
 * A {@link Observable.Transformer} that is used to set the schedulers for an Observable that can
 * be subscribed by {@link MvpLceRxPresenter}.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface SchedulerTransformer<T> extends Observable.Transformer<T, T> {
}
