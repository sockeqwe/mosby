package com.hannesdorfmann.mosby.mvp.sce;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.R;

/**
 * Created by leonardo on 24/10/16.
 */

public abstract class MvpSceFragment<CV extends View, M, V extends MvpSceView<M>, P extends MvpPresenter<V>>
        extends MvpFragment<V,P> implements MvpSceView<M> {

    protected View loadingView;
    protected CV formView;
    protected TextView errorView;

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingView = view.findViewById(R.id.loadingView);
        formView = (CV) view.findViewById(R.id.formView);
        errorView = (TextView) view.findViewById(R.id.errorView);

        if (loadingView == null) {
            throw new NullPointerException(
                    "Loading view is null! Have you specified a loading view in your layout xml file?"
                            + " You have to give your loading View the id R.id.loadingView");
        }

        if (formView == null) {
            throw new NullPointerException(
                    "Form view is null! Have you specified a form view in your layout xml file?"
                            + " You have to give your form View the id R.id.formView");
        }

        if (errorView == null) {
            throw new NullPointerException(
                    "Error view is null! Have you specified an error view in your layout xml file?"
                            + " You have to give your error View the id R.id.errorView");
        }

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onErrorViewClicked();
            }
        });
    }

    protected void onErrorViewClicked() {

    }

    @Override public void showLoading() {
        animateLoadingViewIn();
    }

    /**
     * Override this method if you want to provide your own animation for showing the loading view
     */
    protected void animateLoadingViewIn() {
        SceAnimator.showLoading(loadingView, formView, errorView);
    }

    @Override public void showForm() {
        animateFormViewIn();
    }

    /**
     * Called to animate from loading view to content view
     */
    protected void animateFormViewIn() {
        SceAnimator.showContent(loadingView, formView, errorView);
    }

    /**
     * Get the error message for a certain Exception that will be shown on {@link
     * #showError(Throwable)}
     */
    protected abstract String getErrorMessage(Throwable e);

    @Override public void showError(Throwable e) {
        String errorMsg = getErrorMessage(e);
        errorView.setText(errorMsg);
        animateErrorViewIn();
    }

    @Override
    public void showSucceeded() {
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    /**
     * Animates the error view in (instead of displaying content view / loading view)
     */
    protected void animateErrorViewIn() {
        SceAnimator.showErrorView(loadingView, formView, errorView);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadingView = null;
        formView = null;
        errorView.setOnClickListener(null);
        errorView = null;
    }
}
