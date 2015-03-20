package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateManager;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateSupport;

/**
 * @author Hannes Dorfmann
 */
public abstract class MvpLceViewStateActivity<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpLceActivity<CV, M, V, P> implements MvpLceView<M>, ViewStateSupport<V> {

  protected ParcelableViewState viewState;
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

  @Override public ParcelableViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    if (!(viewState instanceof ParcelableViewState)) {
      throw new IllegalArgumentException(
          "Only " + ParcelableViewState.class.getSimpleName() + " are allowed");
    }

    this.viewState = (ParcelableViewState) viewState;
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

  public abstract ParcelableViewState createViewState();
}
