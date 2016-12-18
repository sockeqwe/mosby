/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvi.view.home;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public interface HomeViewState {

  /**
   * Indicates that the first page is loading
   */
  public final class FirstPageLoadingState implements HomeViewState {

    @Override public String toString() {
      return "FirstPageLoadingState{}";
    }
  }

  /**
   * Indicates that an error has occurred while loading the first page
   */
  public final class FirstPageErrorState implements HomeViewState {
    private final Throwable error;

    public FirstPageErrorState(Throwable error) {
      this.error = error;
    }

    public Throwable getError() {
      return error;
    }

    @Override public String toString() {
      return "FirstPageErrorState{" +
          "error=" + error +
          '}';
    }
  }

  public final class DataState implements HomeViewState {
    private final List<FeedItem> data;
    private final boolean loadingNextPage;
    private final Throwable nextPageError;
    private final boolean loadingPullToRefresh;
    private final Throwable pullToRefreshError;

    public DataState(List<FeedItem> data, boolean loadingNextPage, Throwable nextPageError,
        boolean loadingPullToRefresh, Throwable pullToRefreshError) {
      this.data = data;
      this.loadingNextPage = loadingNextPage;
      this.nextPageError = nextPageError;
      this.loadingPullToRefresh = loadingPullToRefresh;
      this.pullToRefreshError = pullToRefreshError;
    }

    public List<FeedItem> getData() {
      return data;
    }

    public boolean isLoadingNextPage() {
      return loadingNextPage;
    }

    public Throwable getNextPageError() {
      return nextPageError;
    }

    public boolean isLoadingPullToRefresh() {
      return loadingPullToRefresh;
    }

    public Throwable getPullToRefreshError() {
      return pullToRefreshError;
    }

    @Override public String toString() {
      return "DataState{" +
          "data=" + data +
          ", loadingNextPage=" + loadingNextPage +
          ", nextPageError=" + nextPageError +
          ", loadingPullToRefresh=" + loadingPullToRefresh +
          ", pullToRefreshError=" + pullToRefreshError +
          '}';
    }
  }
}
