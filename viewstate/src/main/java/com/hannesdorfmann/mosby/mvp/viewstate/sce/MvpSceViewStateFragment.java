package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.FragmentMvpDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.FragmentMvpViewStateDelegateImpl;
import com.hannesdorfmann.mosby.mvp.delegate.MvpViewStateDelegateCallback;
import com.hannesdorfmann.mosby.mvp.sce.MvpSceFragment;
import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * Created by leonardo on 24/10/16.
 */

public abstract class MvpSceViewStateFragment<CV extends View, M, V extends MvpSceView<M>, P extends MvpPresenter<V>>
    extends MvpSceFragment<CV, M, V, P> implements MvpSceView<M>, MvpViewStateDelegateCallback<V,P> {

    /**
     * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
     * #onViewCreated(View, Bundle)}. Don't instantiate it by hand.
     */
    protected SceViewState<M,V> viewState;

    /**
     * A flag that indicates if the viewstate tires to restore the view right now.
     */
    private boolean restoringViewState = false;

    /**
     * Create the view state object of this class
     */
    public abstract ViewState<V> createViewState();

    @NonNull
    @Override
    protected FragmentMvpDelegate<V, P> getMvpDelegate() {
        if(mvpDelegate == null)
            mvpDelegate = new FragmentMvpViewStateDelegateImpl<>(this);

        return mvpDelegate;
    }

    @Override
    public ViewState<V> getViewState() {
        return viewState;
    }

    public void setViewState(SceViewState<M, V> viewState) {
        this.viewState = viewState;
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

    @Override
    public void setRestoringViewState(boolean restoringViewState) {
        this.restoringViewState = restoringViewState;
    }

    @Override
    public boolean isRestoringViewState() {
        return restoringViewState;
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {

    }

    @Override
    public void onNewViewStateInstance() {
        showForm();
    }
}
