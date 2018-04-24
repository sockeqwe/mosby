package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.viewgroup;

import android.content.Context;
import android.util.AttributeSet;

import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestView;
import com.hannesdorfmann.mosby3.mvi.layout.MviFrameLayout;

public class ViewGroupFinishOnCreateLayout extends MviFrameLayout<LifecycleTestView, LifecycleTestPresenter> implements LifecycleTestView {

    public LifecycleTestPresenter presenter;
    public int createPresenterInvocations;

    public ViewGroupFinishOnCreateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LifecycleTestPresenter createPresenter() {
        createPresenterInvocations++;
        presenter = new LifecycleTestPresenter();
        return presenter;
    }
}
