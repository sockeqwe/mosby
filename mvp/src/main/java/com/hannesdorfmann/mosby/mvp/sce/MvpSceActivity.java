package com.hannesdorfmann.mosby.mvp.sce;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.R;
import com.hannesdorfmann.mosby.mvp.lce.LceAnimator;

/**
 * Created by leonardo on 19/10/16.
 */

public abstract class MvpSceActivity<CV extends View, M, V extends MvpSceView, P extends MvpPresenter<V>>
        extends MvpActivity<V,P> implements MvpSceView<M> {

    protected View loadingView;
    protected CV formView;
    protected TextView errorView;

    @CallSuper
    @Override public void onContentChanged() {
        super.onContentChanged();
        loadingView = findViewById(R.id.loadingView);
        formView = (CV) findViewById(R.id.formView);
        errorView = (TextView) findViewById(R.id.errorView);

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

    /**
     * The default behaviour is to display a toast message as light error (i.e. pull-to-refresh
     * error).
     * Override this method if you want to display the light error in another way (like crouton).
     */
    protected void showLightError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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

}
