package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * Created by leonardo on 23/10/16.
 */

public interface SceViewState<D, V extends MvpSceView<D>> extends ViewState<V> {
    /**
     * Used as currentViewState to indicate that loading is currently displayed on screen
     */
    int STATE_SHOW_LOADING = 0;
    /**
     * Used as currentViewState to indicate that the content is currently displayed on
     * screen
     */
    int STATE_SHOW_FORM = 1;
    /**
     * Used as currentViewState to indicate that the error is currently displayed on screen
     */
    int STATE_SHOW_ERROR = -1;

    /**
     * Set the view state to showing content
     *
     */
    void setStateShowForm();

    /**
     * Set the view state to showing the errorview
     *
     * @param e The reason why the errorview is displayed on screen
     */
    void setStateShowError(Throwable e);

    /**
     * Set the state to show loading
     *
     */
    void setStateShowLoading();
}
