package com.hannesdorfmann.mosby3.mvi.integrationtest.eager;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.Observable;

/**
 *
 */
public interface EagerView extends MvpView {

    Observable<String> intent1();

    Observable<String> intent2();

    void render(String state);
}
