package com.hannesdorfmann.mosby.mvp.rx.lce.scheduler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The default {@link SchedulerTransformer} that subscrubes on {@link Schedulers#newThread()} and
 * observes on Android's main Thread.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class AndroidSchedulerTransformer<T> implements SchedulerTransformer<T> {

  @Override public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
  }
}
