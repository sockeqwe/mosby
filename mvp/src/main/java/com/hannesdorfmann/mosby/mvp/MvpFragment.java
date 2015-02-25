package com.hannesdorfmann.mosby.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.hannesdorfmann.mosby.MosbyFragment;

/**
 * A {@link MosbyFragment} that uses an {@link MvpPresenter} to implement a Model-View-Presenter
 * architecture
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpFragment<P extends MvpPresenter> extends MosbyFragment implements MvpView {

  protected P presenter;

  /**
   * Creates a new presenter instance, if needed. Will reuse the previous presenter instance if
   * {@link #setRetainInstance(boolean)} is set to true. This method will be called after the view
   * has been created and before {@link #init(View, Bundle)} is called.
   */
  protected abstract P createPresenter();

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    // Create the presenter if needed
    if (presenter == null) {
      presenter = createPresenter();
    }
    presenter.setView(this);

    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    presenter.onDestroy(getRetainInstance());
  }
}
