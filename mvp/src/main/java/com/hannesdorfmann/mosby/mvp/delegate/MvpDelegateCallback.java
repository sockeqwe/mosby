package com.hannesdorfmann.mosby.mvp.delegate;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public interface MvpDelegateCallback<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * Creates the presenter instance
   *
   * @return the created presenter instance
   */
  public P createPresenter();

  /**
   * Get the presenter. If null is returned, then a internally a new presenter instance gets
   * created
   * by calling {@link #createPresenter()}
   *
   * @return the presenter instance. can be null.
   */
  public P getPresenter();

  /**
   * Sets the presenter instance
   *
   * @param presenter The presenter instance
   */
  public void setPresenter(P presenter);

  /**
   * Get the MvpView for the presenter
   *
   * @return The view associated with the presenter
   */
  public V getView();

  /**
   * Is the view retaining? This boolean flag is used for {@link MvpPresenter#detachView(boolean)}
   * as parameter.
   *
   * @return true if the view is retaining, hence the presenter should be retaining as well.
   */
  public boolean isRetainingInstance();
}
