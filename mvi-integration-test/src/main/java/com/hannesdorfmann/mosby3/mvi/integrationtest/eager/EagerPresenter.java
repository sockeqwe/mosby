package com.hannesdorfmann.mosby3.mvi.integrationtest.eager;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hannes on 02.03.18.
 */

public class EagerPresenter extends MviBasePresenter<EagerView, String> {

    @Override
    protected void bindIntents() {

        Observable<String> intent1 = intent(new ViewIntentBinder<EagerView, String>() {
            @NonNull
            @Override
            public Observable<String> bind(@NonNull EagerView view) {
                return view.intent1();
            }
        }).concatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.sleep(300);
                        return s + " - Result 1";
                    }
                }).subscribeOn(Schedulers.io());
            }
        });


        Observable<String> intent2 = intent(new ViewIntentBinder<EagerView, String>() {
            @NonNull
            @Override
            public Observable<String> bind(@NonNull EagerView view) {
                return view.intent2();
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.just(s + " - Result 2").subscribeOn(Schedulers.io());
            }
        });


        Observable<String> data = Observable.concat(intent1, intent2)
                .observeOn(AndroidSchedulers.mainThread());

        subscribeViewState(data, new ViewStateConsumer<EagerView, String>() {
            @Override
            public void accept(@NonNull EagerView view, @NonNull String viewState) {
                view.render(viewState);
            }
        });

    }
}
