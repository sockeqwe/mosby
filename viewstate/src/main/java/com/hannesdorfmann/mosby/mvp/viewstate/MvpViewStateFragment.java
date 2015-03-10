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
    implements ViewStateable {

  /**
   * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
   * #onViewCreated(View, Bundle)}. Don't instantiate it by hand.
   */
  protected ViewState viewState;

  /**
   * Create the view state object of this class
   */
  public abstract ViewState createViewState();

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    boolean viewStateRestored = ViewStateManager.createOrRestore(this, this, savedInstanceState);
    if (!viewStateRestored) {
      onEmptyViewState();
    }
  }

  @Override
  public void onEmptyViewState() {

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    ViewStateManager.saveInstanceState(this, outState);
  }

  @Override public ViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState viewState) {
    this.viewState = viewState;
  }
}
