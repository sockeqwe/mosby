package com.hannesdorfmann.mosby3.mvp.integrationtest;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mvp_integration_test.R;

public class ViewStateFinishOnCreateActivity extends MvpViewStateActivity<LifecycleTestView, LifecycleTestPresenter, LifecycleTestViewState> implements LifecycleTestView {

    public LifecycleTestPresenter presenter;
    public int createPresenterInvocation = 0;
    public int createViewStateInvocation = 0;
    public LifecycleTestViewState viewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_state);
        finish();
    }

    @NonNull
    @Override
    public LifecycleTestPresenter createPresenter() {
        presenter = new LifecycleTestPresenter();
        createPresenterInvocation++;
        return presenter;
    }

    @NonNull
    @Override
    public LifecycleTestViewState createViewState() {
        viewState = new LifecycleTestViewState();
        createViewStateInvocation++;
        return viewState;
    }

    @Override
    public void onNewViewStateInstance() {

    }
}
