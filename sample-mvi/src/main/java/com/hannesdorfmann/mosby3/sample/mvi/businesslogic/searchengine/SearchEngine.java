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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.searchengine;

import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.List;

/**
 * With this class you can search for products
 *
 * @author Hannes Dorfmann
 */
public class SearchEngine {
  private final ProductBackendApiDecorator backend;

  public SearchEngine(ProductBackendApiDecorator productApi) {
    this.backend = productApi;
  }

  public Observable<List<Product>> searchFor(@NonNull String searchQueryText) {

    if (searchQueryText == null) {
      return Observable.error(new NullPointerException("SearchQueryText == null"));
    }

    if (searchQueryText.length() == 0) {
      return Observable.error(new IllegalArgumentException("SearchQueryTest is blank"));
    }

    return backend.getAllProducts()
        .flatMap(Observable::fromIterable)
        .filter(product -> isProductMatchingSearchCriteria(product, searchQueryText))
        .toList()
        .toObservable();
  }

  /**
   * Filters those items that contains the search query text in name, description or category
   */
  private boolean isProductMatchingSearchCriteria(Product product, String searchQueryText) {
    String words[] = searchQueryText.split(" ");
    for (String w : words) {
      if (product.getName().contains(w)) return true;
      if (product.getDescription().contains(w)) return true;
      if (product.getCategory().contains(w)) return true;
    }
    return false;
  }
}
