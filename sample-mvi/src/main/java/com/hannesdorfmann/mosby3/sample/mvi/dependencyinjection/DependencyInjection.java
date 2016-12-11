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

package com.hannesdorfmann.mosby3.sample.mvi.dependencyinjection;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductApi;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.searchengine.SearchEngine;
import com.hannesdorfmann.mosby3.sample.mvi.view.search.SearchPresenter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * This is just a very simple example that creates dependency injection.
 * In a real project you might would like to use dagger.
 *
 * @author Hannes Dorfmann
 */
public class DependencyInjection {

  private final Retrofit retrofit = new Retrofit.Builder().baseUrl(ProductApi.BASE_URL)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create())
      .build();

  private final ProductApi productApi = retrofit.create(ProductApi.class);

  /**
   * Returns the {@link ProductApi} to load data over http.
   * This is a singleton
   */
  public ProductApi getProductApi() {
    return productApi;
  }

  SearchEngine newSearchEngine() {
    return new SearchEngine(productApi);
  }

  public SearchPresenter newSearchPresenter() {
    return new SearchPresenter(newSearchEngine());
  }
}
