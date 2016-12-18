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

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.AdditionalItemsLoadable;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.SectionHeader;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a {@link PagingFeedLoader} but groups the resulting list of products into categories.
 * Assumption: Since we support pagination, we assume that there are not items of the same group on
 * different pages in the data feed retrieved from backend
 *
 * @author Hannes Dorfmann
 */
public class GroupedPagedFeedLoader {
  private final PagingFeedLoader feedLoader;
  private final int collapsedGroupProductItemCount = 3;
  private Observable<List<FeedItem>> newestPage;

  public GroupedPagedFeedLoader(PagingFeedLoader feedLoader) {
    this.feedLoader = feedLoader;
  }

  public Observable<List<FeedItem>> getGroupedFirstPage() {
    return getGroupedNextPage();
  }

  public Observable<List<FeedItem>> getGroupedNextPage() {
    return feedLoader.nextPage()
        .flatMap(Observable::fromIterable)
        .groupBy(Product::getCategory)
        .flatMap(groupedByCategory -> groupedByCategory.toList().map(productsInGroup -> {
          List<FeedItem> items = new ArrayList<FeedItem>();
          items.add(new SectionHeader(groupedByCategory.getKey()));
          if (collapsedGroupProductItemCount < productsInGroup.size()) {
            for (int i = 0; i < collapsedGroupProductItemCount; i++) {
              items.add(productsInGroup.get(i));
            }
            items.add(new AdditionalItemsLoadable(
                productsInGroup.size() - collapsedGroupProductItemCount));
          } else {
            items.addAll(productsInGroup);
          }
          return items;
        }).toObservable())
        .concatMap(Observable::fromIterable)
        .toList()
        .toObservable();
  }

  public Observable<List<FeedItem>> getNewestPage() {
    return newestPage;
  }
}
