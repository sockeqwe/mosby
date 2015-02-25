package com.hannesdorfmann.mosby.mvp.lce;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A {@link MvpView} that assumes that there are 3 display operation:
 * <ul>
 * <li>{@link #showLoading(boolean)}: Display a loading animation while loading data in background
 * by
 * invoking the corresponding presenter method</li>
 *
 * <li>{@link #showError(Exception, boolean)}: Display a error view (a TextView) on the screen if
 * an error has occurred while loading data. You can distinguish between a pull-to-refresh error by
 * checking the boolean parameter and display the error message in another, more suitable way like
 * a
 * Toast</li>
 *
 * <li>{@link #showContent()}: After the content has been loaded the presenter calls {@link
 * #setData(Object)} to fill the view with data. Afterwards, the presenter calls {@link
 * #showContent()} to display the data</li>
 * </ul>
 *
 * <p>
 * To make everything work automatically the concre
 * </p>
 *
 * @param <M> The underlying data model
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface MvpLceView<M> extends MvpView {

  /**
   * Display a loading view while loading data in background.
   * <b>The loading view must have the id = R.id.loadingView</b>
   * @param pullToRefresh true, if pull-to-refresh has been invoked loading.
   */
  public void showLoading(boolean pullToRefresh);

  /**
   * Show the content view.
   *
   * <b>The content view must have the id = R.id.contentView</b>
   */
  public void showContent();

  /**
   * Show the error view.
   * <b>The error view must be a TextView with the id = R.id.errorView</b>
   *
   * @param e The Exception that has caused this error
   * @param pullToRefresh true, if the exception was thrown during pull-to-refresh, otherwise
   * false.
   */
  public void showError(Exception e, boolean pullToRefresh);

  /**
   * The data that should be displayed with {@link #showContent()}
   */
  public void setData(M data);
}
