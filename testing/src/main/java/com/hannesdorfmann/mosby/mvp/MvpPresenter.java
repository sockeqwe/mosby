package com.hannesdorfmann.mosby.mvp;

/**
 * The base interface for each mvp presenter
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface MvpPresenter<V extends MvpView> {

  /**
   * Set or attach the view to this presenter
   */
  public void attachView(V view);

  /**
   * Will be called if the view has been destroyed. Typically this method will be invoked from
   * <code>Activity.detachView()</code> or <code>Fragment.onDestroyView()</code>
   */
  public void detachView(boolean retainInstance);
}