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

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import io.reactivex.Observable;

/**
 * The HomeView responsible to display a list of {@link FeedItem}
 *
 * @author Hannes Dorfmann
 */
public interface HomeView extends MvpView {

  /**
   * The intent to load the first page
   *
   * @return The emitted item boolean can be ignored because it is always true
   */
  public Observable<Boolean> loadFirstPageIntent();

  /**
   * The intent to load the next page
   *
   * @return The emitted item boolean can be ignored because it is always true
   */
  public Observable<Boolean> loadNextPageIntent();

  /**
   * The intent to react on pull-to-refresh
   *
   * @return The emitted item boolean can be ignored because it is always true
   */
  public Observable<Boolean> pullToRefreshIntent();

  /**
   * The intent to load more items from a given group
   *
   * @return Observable with the name of the group
   */
  public Observable<String> loadAllProductsFromCategoryIntent();

  /**
   * Renders the viewState
   */
  public void render(HomeViewState viewState);
}
