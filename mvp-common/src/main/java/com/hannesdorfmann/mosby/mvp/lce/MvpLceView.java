/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.mvp.lce;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A {@link MvpView} that assumes that there are 3 display operation:
 * <ul>
 * <li>{@link #showLoading(boolean)}: Display a loading animation while loading data in background
 * by
 * invoking the corresponding presenter method</li>
 *
 * <li>{@link #showError(Throwable, boolean)}: Display a error view (a TextView) on the screen if
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
 * @param <M> The underlying data model
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface MvpLceView<M> extends MvpView {

  /**
   * Display a loading view while loading data in background.
   * <b>The loading view must have the id = R.id.loadingView</b>
   *
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
   * @param e The Throwable that has caused this error
   * @param pullToRefresh true, if the exception was thrown during pull-to-refresh, otherwise
   * false.
   */
  public void showError(Throwable e, boolean pullToRefresh);

  /**
   * The data that should be displayed with {@link #showContent()}
   */
  public void setData(M data);

  /**
   * Load the data. Typically invokes the presenter method to load the desired data.
   * <p>
   * <b>Should not be called from presenter</b> to prevent infinity loops. The method is declared
   * in
   * the views interface to add support for view state easily.
   * </p>
   *
   * @param pullToRefresh true, if triggered by a pull to refresh. Otherwise false.
   */
  public void loadData(boolean pullToRefresh);
}
