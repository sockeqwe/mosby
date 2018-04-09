package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestView;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class MviFinishInOnCreateActivity extends MviActivity<LifecycleTestView, LifecycleTestPresenter> implements LifecycleTestView {

    @NonNull
    @Override
    public LifecycleTestPresenter createPresenter() {
        return new LifecycleTestPresenter();
    }

    public Subject<Boolean> onDestroyReached  = BehaviorSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish(); // finish imeediately is what we would like to test --> App should not crash
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyReached.onNext(true);
        onDestroyReached.onComplete();
    }
}
