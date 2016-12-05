package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsLceViewState;

/**
 * Created by leonardo on 23/10/16.
 */

/**
 * A base view state implementation for {@link SceViewState} (Loading-Content-Error)
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Leonardo Ferrari
 * @since 3.0.0
 */
public abstract class AbsSceViewState<D, V extends MvpSceView<D>> implements SceViewState<D, V> {
    /**
     * The current viewstate. Used to identify if the view is/was showing loading, error, or content.
     */
    protected int currentViewState;
    protected Throwable exception;
    protected D data;

    @Override
    public void setStateShowForm() {
        currentViewState = STATE_SHOW_FORM;
        exception = null;
    }

    @Override
    public void setStateShowLoading() {
        currentViewState = STATE_SHOW_LOADING;
        exception = null;
    }

    @Override
    public void setStateShowError(Throwable e) {
        currentViewState = STATE_SHOW_ERROR;
        exception = e;
    }

    @Override
    public void apply(V view, boolean retained) {
        switch (currentViewState) {
            case STATE_SHOW_FORM:
                view.showForm();
                break;
            case STATE_SHOW_ERROR:
                view.showError(exception);
                break;
            case STATE_SHOW_LOADING:
                if(retained)
                    view.showLoading();
                else
                    view.sendForm(data);
                break;
        }
    }
}
