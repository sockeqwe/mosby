package com.hannesdorfmann.mosby.mvp.delegate;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * This is just the internal implementation for the delegate. Don't use it by your own.
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
class MvpInternalDelegate<V extends MvpView, P extends MvpPresenter<V>> {

  private MvpDelegateCallback<V, P> delegateCallback;

  MvpInternalDelegate(MvpDelegateCallback<V, P> delegateCallback) {
    this.delegateCallback = delegateCallback;
  }

  /**
   * Called  to create the presenter (if no other one already exisits)
   */
  void createPresenter() {

    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      presenter = delegateCallback.createPresenter();
    }
    if (presenter == null) {
      throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
    }

    delegateCallback.setPresenter(presenter);
  }

  /**
   * Attaches the view to the presenter
   */
  void attachView() {
    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter returned from getPresenter() is null");
    }
    presenter.attachView(delegateCallback.getView());
  }

  /**
   * Called to detach the view from presenter
   */
  void detachView() {
    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter returned from getPresenter() is null");
    }
    presenter.detachView(delegateCallback.isRetainingInstance());
  }
}
