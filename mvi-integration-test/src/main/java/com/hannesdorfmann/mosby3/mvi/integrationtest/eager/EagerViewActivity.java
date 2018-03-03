package com.hannesdorfmann.mosby3.mvi.integrationtest.eager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class EagerViewActivity extends MviActivity<EagerView, EagerPresenter> implements EagerView {

    public ReplaySubject<String> renderedStrings =  ReplaySubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eager_view);

    }

    @Override
    public Observable<String> intent1() {
        return Observable.just("Intent 1").startWith("Before Intent 1");
    }

    @Override
    public Observable<String> intent2() {
        return Observable.just("Intent 2");
    }

    @Override
    public void render(String state) {
        ((TextView) findViewById(R.id.text)).setText(state);
        Log.d("Render", state);
        renderedStrings.onNext(state);
    }

    @NonNull
    @Override
    public EagerPresenter createPresenter() {
        return new EagerPresenter();
    }
}
