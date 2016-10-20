package com.hannesdorfmann.mosby.mvp.sce;

import android.support.annotation.UiThread;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by leonardo on 19/10/16.
 */

public interface MvpSceView<M> extends MvpView {

    /**
     * Display a loading view while loading data in background.
     * <b>The loading view must have the id = R.id.loadingView</b>
     *
     */
    @UiThread
    void showLoading();

    /**
     * Show the content view.
     *
     * <b>The content view must have the id = R.id.formView</b>
     */
    @UiThread
    void showForm();

    /**
     * Show the error view.
     * <b>The error view must be a TextView with the id = R.id.errorView</b>
     *
     * @param e The Throwable that has caused this error
     */
    @UiThread
    void showError(Throwable e);

    /**
     * Load the data. Typically invokes the presenter method to load the desired data.
     * <p>
     * <b>Should not be called from presenter</b> to prevent infinity loops. The method is declared
     * in
     * the views interface to add support for view state easily.
     * </p>
     *
     */
    @UiThread
    void showSucceeded();


    @UiThread
    void sendForm(M data);
}
