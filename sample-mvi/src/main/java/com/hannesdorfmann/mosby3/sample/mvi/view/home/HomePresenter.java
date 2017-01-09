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

import android.support.v4.util.Pair;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed.HomeFeedLoader;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.AdditionalItemsLoadable;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.SectionHeader;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */
public class HomePresenter extends MviBasePresenter<HomeView, HomeViewState> {

  private final HomeFeedLoader feedLoader;

  public HomePresenter(HomeFeedLoader feedLoader) {
    this.feedLoader = feedLoader;
  }

  @Override protected void bindIntents() {

    Observable<PartialHomeViewState> loadFirstPage = intent(HomeView::loadFirstPageIntent)
        .doOnNext(ignored -> Timber.d("intent: load first page"))
        .flatMap(ignored -> feedLoader.loadFirstPage()
            .map(items -> (PartialHomeViewState) new PartialHomeViewState.FirstPageLoaded(items))
            .startWith(new PartialHomeViewState.FirstPageLoading())
            .onErrorReturn(PartialHomeViewState.FirstPageError::new)
            .subscribeOn(Schedulers.io()));

    Observable<PartialHomeViewState> nextPage =
        intent(HomeView::loadNextPageIntent)
            .doOnNext(ignored -> Timber.d("intent: load next page"))
            .flatMap(ignored -> feedLoader.loadNextPage()
                .map(items -> (PartialHomeViewState) new PartialHomeViewState.NextPageLoaded(items))
                .startWith(new PartialHomeViewState.NextPageLoading())
                .onErrorReturn(PartialHomeViewState.NexPageLoadingError::new)
                .subscribeOn(Schedulers.io()));

    Observable<PartialHomeViewState> pullToRefreshObservable =
        intent(HomeView::pullToRefreshIntent)
            .doOnNext(ignored -> Timber.d("intent: pull to refresh"))
            .flatMap(ignored -> feedLoader.loadNewestPage()
                .subscribeOn(Schedulers.io())
                .map(items -> (PartialHomeViewState) new PartialHomeViewState.PullToRefreshLoaded(
                    items))
                .startWith(new PartialHomeViewState.PullToRefreshLoading())
                .onErrorReturn(PartialHomeViewState.PullToRefeshLoadingError::new));

    Observable<PartialHomeViewState> loadMoreFromGroup =
        intent(HomeView::loadAllProductsFromCategoryIntent)
            .doOnNext(categoryName -> Timber.d("intent: load more from category %s", categoryName))
            .flatMap(categoryName -> feedLoader.loadProductsOfGroup(categoryName)
                .subscribeOn(Schedulers.io())
                .map(
                    products -> (PartialHomeViewState) new PartialHomeViewState.ProductsOfCategoriesLoaded(
                        categoryName, products))
                .startWith(new PartialHomeViewState.ProductsOfCategoriesLoading(categoryName))
                .onErrorReturn(
                    error -> new PartialHomeViewState.ProductsOfCategoriesLoadingError(categoryName,
                        error)));

    Observable<PartialHomeViewState> partialViewStateFromIntentsObservable =
        Observable.merge(loadFirstPage, nextPage, pullToRefreshObservable, loadMoreFromGroup)
            .observeOn(AndroidSchedulers.mainThread());

    HomeViewState initialState = new HomeViewState.Builder().firstPageLoading(true).build();

    subscribeViewState(
        partialViewStateFromIntentsObservable.scan(initialState, this::viewStateReducer),
        HomeView::render);
  }

  private HomeViewState viewStateReducer(HomeViewState previousState,
      PartialHomeViewState newState) {

    if (newState instanceof PartialHomeViewState.FirstPageLoading) {
      return previousState.builder().firstPageLoading(true).firstPageError(null).build();
    }

    if (newState instanceof PartialHomeViewState.FirstPageError) {
      return previousState.builder()
          .firstPageLoading(false)
          .firstPageError(((PartialHomeViewState.FirstPageError) newState).getError())
          .build();
    }

    if (newState instanceof PartialHomeViewState.FirstPageLoaded) {
      return previousState.builder()
          .firstPageLoading(false)
          .firstPageError(null)
          .data(((PartialHomeViewState.FirstPageLoaded) newState).getData())
          .build();
    }

    if (newState instanceof PartialHomeViewState.NextPageLoading) {
      return previousState.builder().nextPageLoading(true).nextPageError(null).build();
    }

    if (newState instanceof PartialHomeViewState.NexPageLoadingError) {
      return previousState.builder()
          .nextPageLoading(false)
          .nextPageError(((PartialHomeViewState.NexPageLoadingError) newState).getError())
          .build();
    }

    if (newState instanceof PartialHomeViewState.NextPageLoaded) {
      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialHomeViewState.NextPageLoaded) newState).getData().size());
      data.addAll(previousState.getData());
      data.addAll(((PartialHomeViewState.NextPageLoaded) newState).getData());

      return previousState.builder().nextPageLoading(false).nextPageError(null).data(data).build();
    }

    if (newState instanceof PartialHomeViewState.PullToRefreshLoading) {
      return previousState.builder().pullToRefreshLoading(true).pullToRefreshError(null).build();
    }

    if (newState instanceof PartialHomeViewState.PullToRefeshLoadingError) {
      return previousState.builder()
          .pullToRefreshLoading(false)
          .pullToRefreshError(((PartialHomeViewState.PullToRefeshLoadingError) newState).getError())
          .build();
    }

    if (newState instanceof PartialHomeViewState.PullToRefreshLoaded) {
      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialHomeViewState.PullToRefreshLoaded) newState).getData().size());
      data.addAll(((PartialHomeViewState.PullToRefreshLoaded) newState).getData());
      data.addAll(previousState.getData());
      return previousState.builder()
          .pullToRefreshLoading(false)
          .pullToRefreshError(null)
          .data(data)
          .build();
    }

    if (newState instanceof PartialHomeViewState.ProductsOfCategoriesLoading) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialHomeViewState.ProductsOfCategoriesLoading) newState).getCategoryName(),
          previousState.getData());
      AdditionalItemsLoadable foundItem = found.second;
      AdditionalItemsLoadable toInsert =
          new AdditionalItemsLoadable(foundItem.getMoreItemsAvailableCount(),
              foundItem.getCategoryName(), true, null);

      List<FeedItem> data = new ArrayList<>(previousState.getData().size());
      data.addAll(previousState.getData());
      data.set(found.first, toInsert);

      return previousState.builder().data(data).build();
    }

    if (newState instanceof PartialHomeViewState.ProductsOfCategoriesLoadingError) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialHomeViewState.ProductsOfCategoriesLoadingError) newState).getCategoryName(),
          previousState.getData());

      AdditionalItemsLoadable foundItem = found.second;
      AdditionalItemsLoadable toInsert =
          new AdditionalItemsLoadable(foundItem.getMoreItemsAvailableCount(),
              foundItem.getCategoryName(), false,
              ((PartialHomeViewState.ProductsOfCategoriesLoadingError) newState).getError());

      List<FeedItem> data = new ArrayList<>(previousState.getData().size());
      data.addAll(previousState.getData());
      data.set(found.first, toInsert);

      return previousState.builder().data(data).build();
    }

    if (newState instanceof PartialHomeViewState.ProductsOfCategoriesLoaded) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialHomeViewState.ProductsOfCategoriesLoaded) newState).getCategoryName(),
          previousState.getData());

      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialHomeViewState.ProductsOfCategoriesLoaded) newState).getData().size());
      data.addAll(previousState.getData());

      // Search for the section header
      int sectionHeaderIndex = -1;
      for (int i = found.first; i >= 0; i--) {
        FeedItem item = previousState.getData().get(i);
        if (item instanceof SectionHeader && ((SectionHeader) item).getName()
            .equals(
                ((PartialHomeViewState.ProductsOfCategoriesLoaded) newState).getCategoryName())) {
          sectionHeaderIndex = i;
          break;
        }

        // Remove all items of that category. The new list of products will be added afterwards
        data.remove(i);
      }

      if (sectionHeaderIndex < 0) {
        throw new RuntimeException("Couldn't find the section header for category "
            + ((PartialHomeViewState.ProductsOfCategoriesLoaded) newState).getCategoryName());
      }

      data.addAll(sectionHeaderIndex + 1,
          ((PartialHomeViewState.ProductsOfCategoriesLoaded) newState).getData());

      return previousState.builder().data(data).build();
    }

    throw new IllegalStateException("Don't know how to reduce the partial state " + newState);
  }

  /**
   * find the {@link AdditionalItemsLoadable} for the given category name
   *
   * @param categoryName The name of the category
   * @param items the list of feeditems
   */
  private Pair<Integer, AdditionalItemsLoadable> findAdditionalItems(String categoryName,
      List<FeedItem> items) {
    int size = items.size();
    for (int i = 0; i < size; i++) {
      FeedItem item = items.get(i);
      if (item instanceof AdditionalItemsLoadable
          && ((AdditionalItemsLoadable) item).getCategoryName().equals(categoryName)) {
        return Pair.create(i, (AdditionalItemsLoadable) item);
      }
    }

    throw new RuntimeException("No "
        + AdditionalItemsLoadable.class.getSimpleName()
        + " has been found for category = "
        + categoryName);
  }
}
