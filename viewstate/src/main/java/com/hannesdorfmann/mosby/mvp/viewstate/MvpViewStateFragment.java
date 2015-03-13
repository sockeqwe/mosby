package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * This is a enhancement of {@link com.hannesdorfmann.mosby.mvp.MvpFragment} that introduces the
 * support of {@link com.hannesdorfmann.mosby.mvp.viewstate.ViewState}.
 * <p>
 * You can change the behaviour of what to do if the viewstate is empty (usually if the fragment
 * creates the viewState for the very first time and therefore has no state / data to restore) by
 * overriding {@link #onEmptyViewState()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpViewStateFragment<P extends MvpPresenter> extends MvpFragment<P>
    implements ViewStateSupport {

  /**
   * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
   * #onViewCreated(View, Bundle)}. Don't instantiate it by hand.
   */
  protected ViewState viewState;

  /**
   * Create the view state object of this class
   */
  public abstract ViewState createViewState();

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    createOrRestoreViewState(savedInstanceState);
  }

  /**
   * Creates or restores the viewstate
   *
   * @param savedInstanceState The Bundle that may or may not contain the viewstate
   * @return true if restored successfully, otherwise fals
   */
  protected boolean createOrRestoreViewState(Bundle savedInstanceState) {

    return ViewStateManager.createOrRestore(this, this, savedInstanceState);
  }

  @Override public abstract void onEmptyViewState();

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    saveViewStateInstanceState(outState);
  }

  /**
   * Called from {@link #onSaveInstanceState(Bundle)} to store the bundle persistent
   *
   * @param outState The bundle to store
   */
  protected void saveViewStateInstanceState(Bundle outState) {
    ViewStateManager.saveInstanceState(this, outState);
  }

  @Override public ViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    this.viewState = viewState;
  }
}
