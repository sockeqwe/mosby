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
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public interface PartialHomeViewState {

  /**
   * Indicates that the first page is loading
   */
  public final class FirstPageLoading implements PartialHomeViewState {

    @Override public String toString() {
      return "FirstPageLoadingState{}";
    }
  }

  /**
   * Indicates that an error has occurred while loading the first page
   */
  public final class FirstPageError implements PartialHomeViewState {
    private final Throwable error;

    public FirstPageError(Throwable error) {
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

  /**
   * Indicates that the first page data has been loaded successfully
   */
  public final class FirstPageLoaded implements PartialHomeViewState {
    private final List<FeedItem> data;

    public FirstPageLoaded(List<FeedItem> data) {
      this.data = data;
    }

    public List<FeedItem> getData() {
      return data;
    }
  }

  /**
   * Next Page has been loaded successfully
   */
  public final class NextPageLoaded implements PartialHomeViewState {
    private final List<FeedItem> data;

    public NextPageLoaded(List<FeedItem> data) {
      this.data = data;
    }

    public List<FeedItem> getData() {
      return data;
    }
  }

  /**
   * Error while loading new page
   */
  public final class NexPageLoadingError implements PartialHomeViewState {
    private final Throwable error;

    public NexPageLoadingError(Throwable error) {
      this.error = error;
    }

    public Throwable getError() {
      return error;
    }
  }

  /**
   * Indicates that loading the next page has started
   */
  public final class NextPageLoading implements PartialHomeViewState {
  }

  /**
   * Indicates that loading the newest items via pull to refresh has started
   */
  public final class PullToRefreshLoading implements PartialHomeViewState {
  }

  /**
   * Indicates that an error while loading the newest items via pull to refresh has occurred
   */
  public final class PullToRefeshLoadingError implements PartialHomeViewState {
    private final Throwable error;

    public PullToRefeshLoadingError(Throwable error) {
      this.error = error;
    }

    public Throwable getError() {
      return error;
    }
  }

  /**
   * Indicates that data has been loaded successfully over pull-to-refresh
   */
  public final class PullToRefreshLoaded implements PartialHomeViewState {
    private final List<FeedItem> data;

    public PullToRefreshLoaded(List<FeedItem> data) {
      this.data = data;
    }

    public List<FeedItem> getData() {
      return data;
    }
  }

  /**
   * Loading all Products of a given category has been started
   */
  public final class ProductsOfCategoriesLoading implements PartialHomeViewState {
    private final String categoryName;

    public ProductsOfCategoriesLoading(String categoryName) {
      this.categoryName = categoryName;
    }

    public String getCategoryName() {
      return categoryName;
    }
  }

  /**
   * An error while loading all products has been occurred
   */
  public final class ProductsOfCategoriesLoadingError implements PartialHomeViewState {
    private final String categoryName;
    private final Throwable error;

    public ProductsOfCategoriesLoadingError(String categoryName, Throwable error) {
      this.categoryName = categoryName;
      this.error = error;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public Throwable getError() {
      return error;
    }
  }

  /**
   * Products of a given Category has been loaded
   */
  public final class ProductsOfCategoriesLoaded implements PartialHomeViewState {
    private final List<Product> data;
    private final String categoryName;

    public ProductsOfCategoriesLoaded(String categoryName, List<Product> data) {
      this.data = data;
      this.categoryName = categoryName;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public List<Product> getData() {
      return data;
    }
  }
}
