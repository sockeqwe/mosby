package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * This is a enhancement of {@link com.hannesdorfmann.mosby.mvp.MvpActivity} that introduces the
 * support of {@link com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState}.
 * <p>
 * You can change the behaviour of what to do if the viewstate is empty (usually if the activity
 * creates the viewState for the very first time and therefore has no state / data to restore) by
 * overriding {@link #onNewViewStateInstance()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpViewStateActivity<P extends MvpPresenter> extends MvpActivity<P>
    implements ViewStateSupport {

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

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // not needed. You could override this is subclasses if needed
  }

  public abstract ParcelableViewState createViewState();
}
