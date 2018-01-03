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
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public class PagingFeedLoader {

  private final ProductBackendApiDecorator backend;
  private int currentPage = 1;
  private boolean endReached = false;
  private boolean newestPageLoaded = false;

  public PagingFeedLoader(ProductBackendApiDecorator backend) {
    this.backend = backend;
  }

  public Observable<List<Product>> newestPage() {
    if (newestPageLoaded) {
      return Observable.fromCallable(() -> {
        Thread.sleep(2000);
        return Collections.emptyList();
      });
    }

    return backend.getProducts(0).doOnNext(products -> newestPageLoaded = true);
  }

  public Observable<List<Product>> nextPage() {
    // I know, it's not a pure function nor elegant code
    // but that is not the purpose of this demo.
    // This code should be understandable by everyone.

    if (endReached) {
      return Observable.just(Collections.emptyList());
    }

    return backend.getProducts(currentPage).doOnNext(result -> {
      currentPage++;
      if (result.isEmpty()) {
        endReached = true;
      }
    });
  }
}
