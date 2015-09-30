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

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.R;

/**
 * A {@link MvpActivity} that implements {@link MvpLceView} which gives you 3 options:
 * <ul>
 * <li>Display a loading view: A view with <b>R.id.loadingView</b> must be specified in your
 * inflated xml layout</li>
 * <li>Display a error view: A <b>TextView</b> with <b>R.id.errorView</b> must be declared in your
 * inflated xml layout</li>
 * <li>Display content view: A view witjh <b>R.id.contentView</b> must be specified in your
 * inflated
 * xml layout</li>
 * </ul>
 *
 * @param <CV> The type of the content view with the id = R.id.contentView. Can be any kind of
 * android view widget like ListView, RecyclerView, ScrollView or a simple layout like Framelayout
 * etc. (everything that extends from android.view.View)
 * @param <M> The underlying data model that will be displayed with this view
 * @param <V> The View interface that must be implemented by this view. You can use {@link
 * MvpLceView}, but if you want to add more methods you have to provide your own view interface
 * that
 * extends from {@link MvpLceView}
 * @param <P> The type of the Presenter. Must extend from {@link MvpPresenter}
 */
public abstract class MvpLceActivity<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpActivity<V, P> implements MvpLceView<M> {

  protected View loadingView;
  protected CV contentView;
  protected TextView errorView;

  @CallSuper @Override public void onContentChanged() {
    super.onContentChanged();
    loadingView = findViewById(R.id.loadingView);
    contentView = (CV) findViewById(R.id.contentView);
    errorView = (TextView) findViewById(R.id.errorView);

    if (loadingView == null) {
      throw new NullPointerException(
          "Loading view is null! Have you specified a loading view in your layout xml file?"
              + " You have to give your loading View the id R.id.loadingView");
    }

    if (contentView == null) {
      throw new NullPointerException(
          "Content view is null! Have you specified a content view in your layout xml file?"
              + " You have to give your content View the id R.id.contentView");
    }

    if (errorView == null) {
      throw new NullPointerException(
          "Error view is null! Have you specified a content view in your layout xml file?"
              + " You have to give your error View the id R.id.contentView");
    }

    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onErrorViewClicked();
      }
    });
  }

  /**
   * Called if the error view has been clicked. To disable clicking on the errorView use
   * <code>errorView.setClickable(false)</code>
   */
  protected void onErrorViewClicked() {
    loadData(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {

    if (!pullToRefresh) {
      animateLoadingViewIn();
    }

    // otherwise the pull to refresh widget will already display a loading animation
  }

  /**
   * Override this method if you want to provide your own animation for showing the loading view
   */
  protected void animateLoadingViewIn() {
    LceAnimator.showLoading(loadingView, contentView, errorView);
  }

  @Override public void showContent() {
    animateContentViewIn();
  }

  /**
   * Called to animate from loading view to content view
   */
  protected void animateContentViewIn() {
    LceAnimator.showContent(loadingView, contentView, errorView);
  }

  /**
   * Get the error message for a certain Exception that will be shown on {@link
   * #showError(Throwable, boolean)}
   */
  protected abstract String getErrorMessage(Throwable e, boolean pullToRefresh);

  /**
   * The default behaviour is to display a toast message as light error (i.e. pull-to-refresh
   * error).
   * Override this method if you want to display the light error in another way (like crouton).
   */
  protected void showLightError(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {

    String errorMsg = getErrorMessage(e, pullToRefresh);

    if (pullToRefresh) {
      showLightError(errorMsg);
    } else {
      errorView.setText(errorMsg);
      animateErrorViewIn();
    }
  }

  /**
   * Animates the error view in (instead of displaying content view / loading view)
   */
  protected void animateErrorViewIn() {
    LceAnimator.showErrorView(loadingView, contentView, errorView);
  }
}


