package com.hannesdorfmann.mosby.mvp;

import android.os.Bundle;
import com.hannesdorfmann.mosby.MosbyActivity;

/**
 * A {@link MosbyActivity} that uses an {@link MvpPresenter} to implement a Model-View-Presenter
 * Architecture.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpActivity<P extends MvpPresenter> extends MosbyActivity implements MvpView {

  protected P presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = createPresenter();
    presenter.setView(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.onDestroy(false);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this view
   */
  protected abstract P createPresenter();
}
