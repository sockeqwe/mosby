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

package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

/**
 * A base view state implementation for {@link LceViewState} (Loading-Content-Error)
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class AbsLceViewState<D, V extends MvpLceView<D>> implements LceViewState<D, V> {

  /**
   * The current viewstate. Used to identify if the view is/was showing loading, error, or content.
   */
  protected int currentViewState;
  protected boolean pullToRefresh;
  protected Throwable exception;
  protected D loadedData;

  @Override public void setStateShowContent(D loadedData) {

    currentViewState = STATE_SHOW_CONTENT;
    this.loadedData = loadedData;
    exception = null;
  }

  @Override public void setStateShowError(Throwable e, boolean pullToRefresh) {
    currentViewState = STATE_SHOW_ERROR;
    exception = e;
    this.pullToRefresh = pullToRefresh;
    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, don't clear loaded data, because of pull to refresh where previous data may
    // be displayed while showing error
  }

  @Override public void setStateShowLoading(boolean pullToRefresh) {
    currentViewState = STATE_SHOW_LOADING;
    this.pullToRefresh = pullToRefresh;
    exception = null;

    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, don't clear loaded data, because of pull to refresh where previous data
    // may be displayed while showing error
  }

  @Override public void apply(V view, boolean retained) {

    if (currentViewState == STATE_SHOW_CONTENT) {
      view.setData(loadedData);
      view.showContent();
    } else if (currentViewState == STATE_SHOW_LOADING) {

      boolean ptr = pullToRefresh;
      if (pullToRefresh) {
        view.setData(loadedData);
        view.showContent();
      }

      if (retained) {
        view.showLoading(ptr);
      } else {
        view.loadData(ptr);
      }
    } else if (currentViewState == STATE_SHOW_ERROR) {

      boolean ptr = pullToRefresh;
      Throwable e = exception;
      if (pullToRefresh) {
        view.setData(loadedData);
        view.showContent();
      }
      view.showError(e, ptr);
    }
  }
}
