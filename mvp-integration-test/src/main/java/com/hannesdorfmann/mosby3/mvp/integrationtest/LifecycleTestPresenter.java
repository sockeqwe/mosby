package com.hannesdorfmann.mosby3.mvp.integrationtest;

import android.util.Log;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

public class LifecycleTestPresenter extends MvpBasePresenter<LifecycleTestView> {

    public int attachViewInvokations = 0;
    public LifecycleTestView attachedView;
    public int detachViewInvokations = 0;
    public int destoryInvoations = 0;

    public LifecycleTestPresenter() {
        Log.d(getClass().getSimpleName(), "constructor " + attachViewInvokations + " " + attachedView + " in " + toString());
    }


    @Override
    public void attachView(LifecycleTestView view) {
        super.attachView(view);
        attachViewInvokations++;
        attachedView = view;
        Log.d(getClass().getSimpleName(), "attachView " + attachViewInvokations + " " + attachedView + " in " + toString());
    }

    @Override
    public void detachView() {
        super.detachView();
        attachedView = null;
        detachViewInvokations++;
        Log.d(getClass().getSimpleName(), "detachView " + detachViewInvokations + " in " + toString());
    }

    @Override
    public void destroy() {
        super.destroy();
        destoryInvoations++;
        Log.d(getClass().getSimpleName(), "destroy Presenter " + destoryInvoations + " in " + toString());
    }

}
