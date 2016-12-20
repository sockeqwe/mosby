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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public final class HomeViewState {

  private final boolean loadingFirstPage;
  private final Throwable firstPageError;
  private final List<FeedItem> data;
  private final boolean loadingNextPage;
  private final Throwable nextPageError;
  private final boolean loadingPullToRefresh;
  private final Throwable pullToRefreshError;

  private HomeViewState(List<FeedItem> data, boolean loadingFirstPage, Throwable firstPageError,
      boolean loadingNextPage, Throwable nextPageError, boolean loadingPullToRefresh,
      Throwable pullToRefreshError) {
    this.data = data;
    this.loadingNextPage = loadingNextPage;
    this.nextPageError = nextPageError;
    this.loadingPullToRefresh = loadingPullToRefresh;
    this.pullToRefreshError = pullToRefreshError;
    this.firstPageError = firstPageError;
    this.loadingFirstPage = loadingFirstPage;
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

  public boolean isLoadingFirstPage() {
    return loadingFirstPage;
  }

  public Throwable getFirstPageError() {
    return firstPageError;
  }

  public Builder builder() {
    return new Builder(this);
  }

  @Override public String toString() {
    return "HomeViewState{" + 
        "\nloadingFirstPage=" + loadingFirstPage +
        ",\n firstPageError=" + firstPageError +
        ",\n data=" + data +
        ",\n loadingNextPage=" + loadingNextPage +
        ",\n nextPageError=" + nextPageError +
        ",\n loadingPullToRefresh=" + loadingPullToRefresh +
        ",\n pullToRefreshError=" + pullToRefreshError +
        "\n}";
  }

  public static final class Builder {
    private boolean loadingFirstPage;
    private Throwable firstPageError;
    private List<FeedItem> data;
    private boolean loadingNextPage;
    private Throwable nextPageError;
    private boolean loadingPullToRefresh;
    private Throwable pullToRefreshError;

    public Builder() {
      data = Collections.emptyList();
    }

    public Builder(HomeViewState toCopyFrom) {
      this.data = new ArrayList<>(toCopyFrom.getData().size());
      this.data.addAll(toCopyFrom.getData());
      this.loadingFirstPage = toCopyFrom.isLoadingFirstPage();
      this.loadingNextPage = toCopyFrom.isLoadingNextPage();
      this.loadingNextPage = toCopyFrom.isLoadingNextPage();
      this.nextPageError = toCopyFrom.getNextPageError();
      this.pullToRefreshError = toCopyFrom.getPullToRefreshError();
      this.firstPageError = toCopyFrom.getFirstPageError();
    }

    public Builder firstPageLoading(boolean loadingFirstPage) {
      this.loadingFirstPage = loadingFirstPage;
      return this;
    }

    public Builder firstPageError(Throwable error) {
      this.firstPageError = error;
      return this;
    }

    public Builder data(List<FeedItem> data) {
      this.data = data;
      return this;
    }

    public Builder nextPageLoading(boolean loadingNextPage) {
      this.loadingNextPage = loadingNextPage;
      return this;
    }

    public Builder nextPageError(Throwable error) {
      this.nextPageError = error;
      return this;
    }

    public Builder pullToRefreshLoading(boolean loading) {
      this.loadingPullToRefresh = loading;
      return this;
    }

    public Builder pullToRefreshError(Throwable error) {
      this.pullToRefreshError = error;
      return this;
    }

    public HomeViewState build() {
      return new HomeViewState(data, loadingFirstPage, firstPageError, loadingNextPage,
          nextPageError, loadingPullToRefresh, pullToRefreshError);
    }
  }
}

