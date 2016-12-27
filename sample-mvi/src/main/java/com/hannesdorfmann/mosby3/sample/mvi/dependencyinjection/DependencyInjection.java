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

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed.GroupedPagedFeedLoader;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed.HomeFeedLoader;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed.PagingFeedLoader;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApi;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.searchengine.SearchEngine;
import com.hannesdorfmann.mosby3.sample.mvi.view.category.CategoryPresenter;
import com.hannesdorfmann.mosby3.sample.mvi.view.home.HomePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.view.menu.MainMenuPresenter;
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

  private final Retrofit retrofit = new Retrofit.Builder().baseUrl(ProductBackendApi.BASE_URL)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create())
      .build();

  private final ProductBackendApi backendApi = retrofit.create(ProductBackendApi.class);
  private final ProductBackendApiDecorator backendApiDecorator =
      new ProductBackendApiDecorator(backendApi);
  private final MainMenuPresenter mainMenuPresenter = new MainMenuPresenter(backendApiDecorator);

  SearchEngine newSearchEngine() {
    return new SearchEngine(backendApiDecorator);
  }

  PagingFeedLoader newPagingFeedLoader() {
    return new PagingFeedLoader(backendApiDecorator);
  }

  GroupedPagedFeedLoader newGroupedPagedFeedLoader() {
    return new GroupedPagedFeedLoader(newPagingFeedLoader());
  }

  HomeFeedLoader newHomeFeedLoader() {
    return new HomeFeedLoader(newGroupedPagedFeedLoader(), backendApiDecorator);
  }

  public SearchPresenter newSearchPresenter() {
    return new SearchPresenter(newSearchEngine());
  }

  public HomePresenter newHomePresenter() {
    return new HomePresenter(newHomeFeedLoader());
  }

  /**
   * This is a singleton
   */
  public MainMenuPresenter getMainMenuPresenter() {
    return mainMenuPresenter;
  }

  public CategoryPresenter newCategoryPresenter() {
    return new CategoryPresenter(backendApiDecorator);
  }
}
