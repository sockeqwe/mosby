package com.hannesdorfmann.mosby3.mvp.delegate;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;

/**
 * * An enhanced version of {@link MvpDelegateCallback} that adds {@link ViewState} support.
 * This interface must be implemented by all (subclasses of) android.view.View like FrameLayout
 * that want to support {@link
 * ViewState} and mvp.
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
public interface ViewGroupViewStateDelegateCallback<V extends MvpView, P extends MvpPresenter<V>>
    extends MvpViewStateDelegateCallback<V, P>, ViewGroupDelegateCallback<V, P> {
}
