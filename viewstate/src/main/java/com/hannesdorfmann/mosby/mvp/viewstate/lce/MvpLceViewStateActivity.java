package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateManager;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateSupport;

/**
 * A {@link MvpLceActivity} with {@link ViewState} support.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpLceViewStateActivity<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpLceActivity<CV, M, V, P> implements MvpLceView<M>, ViewStateSupport<V> {

  protected ParcelableLceViewState<M, V> viewState;
  protected boolean restoringViewState = false;

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
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

  @Override public RestoreableViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    if (!(viewState instanceof ParcelableLceViewState)) {
      throw new IllegalArgumentException(
          "Only " + RestoreableViewState.class.getSimpleName() + " are allowed");
    }

    this.viewState = (ParcelableLceViewState<M, V>) viewState;
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.restoringViewState = restoringViewState;
  }

  @Override public boolean isRestoringViewState() {
    return restoringViewState;
  }

  @Override public void onNewViewStateInstance() {
    loadData(false);
  }

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // not needed. You could override this is subclasses if needed
  }

  @Override public void showContent() {
    super.showContent();
    viewState.setStateShowContent(getData());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    viewState.setStateShowError(e, pullToRefresh);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    viewState.setStateShowLoading(pullToRefresh);
  }

  @Override protected void showLightError(String msg) {
    if (isRestoringViewState()) {
      return; // Do not display toast again while restoring viewstate
    }

    super.showLightError(msg);
  }

  /**
   * Creates the viewstate
   * @return
   */
  public abstract ParcelableLceViewState<M, V> createViewState();

  /**
   * Get the data that has been set before in {@link #setData(Object)}
   * <p>
   * <b>It's necessary to return the same data as set before to ensure that {@link ViewState} works
   * correctly</b>
   * </p>
   *
   * @return The data
   */
  public abstract M getData();
}
