package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import android.support.annotation.NonNull;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.ActivityMvpDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.ActivityMvpViewStateDelegateCallback;
import com.hannesdorfmann.mosby.mvp.delegate.ActivityMvpViewStateDelegateImpl;
import com.hannesdorfmann.mosby.mvp.sce.MvpSceActivity;
import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * Created by leonardo on 24/10/16.
 */

public abstract class MvpSceViewStateActivity<CV extends View, M, V extends MvpSceView<M>, P extends MvpPresenter<V>>
    extends MvpSceActivity<CV,M,V,P>
    implements MvpSceView<M>, ActivityMvpViewStateDelegateCallback<V,P> {

    protected SceViewState<M, V> viewState;
    protected boolean restoringViewState = false;

    @NonNull
    @Override
    protected ActivityMvpDelegate<V, P> getMvpDelegate() {
        if(mvpDelegate == null)
            return new ActivityMvpViewStateDelegateImpl<>(this);

        return mvpDelegate;
    }

    public ViewState<V> getViewState() {
        return viewState;
    }

    public void setViewState(ViewState<V> viewState) {
        if (!(viewState instanceof SceViewState)) {
            throw new IllegalArgumentException(
                    "Only " + SceViewState.class.getSimpleName() + " are allowed as view state");
        }

        this.viewState = (SceViewState<M, V>) viewState;
    }

    @Override
    public void setRestoringViewState(boolean restoringViewState) {
        this.restoringViewState = restoringViewState;
    }

    @Override
    public boolean isRestoringViewState() {
        return restoringViewState;
    }

    @Override
    public void onNewViewStateInstance() {
        showForm();
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {

    }

    @Override
    public void showForm() {
        super.showForm();
        viewState.setStateShowForm();
    }

    @Override
    public void showError(Throwable e) {
        super.showError(e);
        viewState.setStateShowError(e);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        viewState.setStateShowLoading();
    }


    public abstract SceViewState<M, V> createViewState();
}
