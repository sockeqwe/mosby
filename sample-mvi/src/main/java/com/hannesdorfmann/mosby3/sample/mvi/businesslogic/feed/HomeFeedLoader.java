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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Builds the HomeFeed
 *
 * @author Hannes Dorfmann
 */
public class HomeFeedLoader {
  private final GroupedPagedFeedLoader groupedLoader;
  private final ProductBackendApiDecorator backendApi;

  public HomeFeedLoader(GroupedPagedFeedLoader groupedLoader,
      ProductBackendApiDecorator backendApi) {
    this.groupedLoader = groupedLoader;
    this.backendApi = backendApi;
  }

  /**
   * Typically triggered with a pull-to-refresh
   */
  public Observable<List<FeedItem>> loadNewestPage() {
    return groupedLoader.getNewestPage().delay(2, TimeUnit.SECONDS);
  }

  /**
   * Loads the first page
   */
  public Observable<List<FeedItem>> loadFirstPage() {
    return groupedLoader.getGroupedFirstPage().delay(2, TimeUnit.SECONDS);
  }

  /**
   * loads the next page (pagination)
   */
  public Observable<List<FeedItem>> loadNextPage() {
    return groupedLoader.getGroupedNextPage().delay(2, TimeUnit.SECONDS);
  }

  /**
   * Loads all items of  a given category
   *
   * @param categoryName the category name
   */
  public Observable<List<Product>> loadProductsOfGroup(String categoryName) {
    return backendApi.getAllProductsOfCategory(categoryName).delay(3, TimeUnit.SECONDS);
  }
}
