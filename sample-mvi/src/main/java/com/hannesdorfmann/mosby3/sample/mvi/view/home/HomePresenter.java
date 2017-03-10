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

    //
    // In a real app this code would rather be moved to an Interactor
    //
    Observable<PartialStateChanges> loadFirstPage = intent(HomeView::loadFirstPageIntent).doOnNext(
        ignored -> Timber.d("intent: load first page"))
        .flatMap(ignored -> feedLoader.loadFirstPage()
            .map(items -> (PartialStateChanges) new PartialStateChanges.FirstPageLoaded(items))
            .startWith(new PartialStateChanges.FirstPageLoading())
            .onErrorReturn(PartialStateChanges.FirstPageError::new)
            .subscribeOn(Schedulers.io()));

    Observable<PartialStateChanges> nextPage =
        intent(HomeView::loadNextPageIntent).doOnNext(ignored -> Timber.d("intent: load next page"))
            .flatMap(ignored -> feedLoader.loadNextPage()
                .map(items -> (PartialStateChanges) new PartialStateChanges.NextPageLoaded(items))
                .startWith(new PartialStateChanges.NextPageLoading())
                .onErrorReturn(PartialStateChanges.NexPageLoadingError::new)
                .subscribeOn(Schedulers.io()));

    Observable<PartialStateChanges> pullToRefresh = intent(HomeView::pullToRefreshIntent).doOnNext(
        ignored -> Timber.d("intent: pull to refresh"))
        .flatMap(ignored -> feedLoader.loadNewestPage()
            .subscribeOn(Schedulers.io())
            .map(items -> (PartialStateChanges) new PartialStateChanges.PullToRefreshLoaded(items))
            .startWith(new PartialStateChanges.PullToRefreshLoading())
            .onErrorReturn(PartialStateChanges.PullToRefeshLoadingError::new));

    Observable<PartialStateChanges> loadMoreFromGroup =
        intent(HomeView::loadAllProductsFromCategoryIntent).doOnNext(
            categoryName -> Timber.d("intent: load more from category %s", categoryName))
            .flatMap(categoryName -> feedLoader.loadProductsOfCategory(categoryName)
                .subscribeOn(Schedulers.io())
                .map(
                    products -> (PartialStateChanges) new PartialStateChanges.ProductsOfCategoryLoaded(
                        categoryName, products))
                .startWith(new PartialStateChanges.ProductsOfCategoryLoading(categoryName))
                .onErrorReturn(
                    error -> new PartialStateChanges.ProductsOfCategoryLoadingError(categoryName,
                        error)));

    Observable<PartialStateChanges> allIntentsObservable =
        Observable.merge(loadFirstPage, nextPage, pullToRefresh, loadMoreFromGroup)
            .observeOn(AndroidSchedulers.mainThread());

    HomeViewState initialState = new HomeViewState.Builder().firstPageLoading(true).build();

    subscribeViewState(
        allIntentsObservable.scan(initialState, this::viewStateReducer).distinctUntilChanged(),
        HomeView::render);
  }

  private HomeViewState viewStateReducer(HomeViewState previousState,
      PartialStateChanges partialChanges) {

    if (partialChanges instanceof PartialStateChanges.FirstPageLoading) {
      return previousState.builder().firstPageLoading(true).firstPageError(null).build();
    }

    if (partialChanges instanceof PartialStateChanges.FirstPageError) {
      return previousState.builder()
          .firstPageLoading(false)
          .firstPageError(((PartialStateChanges.FirstPageError) partialChanges).getError())
          .build();
    }

    if (partialChanges instanceof PartialStateChanges.FirstPageLoaded) {
      return previousState.builder()
          .firstPageLoading(false)
          .firstPageError(null)
          .data(((PartialStateChanges.FirstPageLoaded) partialChanges).getData())
          .build();
    }

    if (partialChanges instanceof PartialStateChanges.NextPageLoading) {
      return previousState.builder().nextPageLoading(true).nextPageError(null).build();
    }

    if (partialChanges instanceof PartialStateChanges.NexPageLoadingError) {
      return previousState.builder()
          .nextPageLoading(false)
          .nextPageError(((PartialStateChanges.NexPageLoadingError) partialChanges).getError())
          .build();
    }

    if (partialChanges instanceof PartialStateChanges.NextPageLoaded) {
      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialStateChanges.NextPageLoaded) partialChanges).getData().size());
      data.addAll(previousState.getData());
      data.addAll(((PartialStateChanges.NextPageLoaded) partialChanges).getData());

      return previousState.builder().nextPageLoading(false).nextPageError(null).data(data).build();
    }

    if (partialChanges instanceof PartialStateChanges.PullToRefreshLoading) {
      return previousState.builder().pullToRefreshLoading(true).pullToRefreshError(null).build();
    }

    if (partialChanges instanceof PartialStateChanges.PullToRefeshLoadingError) {
      return previousState.builder()
          .pullToRefreshLoading(false)
          .pullToRefreshError(
              ((PartialStateChanges.PullToRefeshLoadingError) partialChanges).getError())
          .build();
    }

    if (partialChanges instanceof PartialStateChanges.PullToRefreshLoaded) {
      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialStateChanges.PullToRefreshLoaded) partialChanges).getData().size());
      data.addAll(((PartialStateChanges.PullToRefreshLoaded) partialChanges).getData());
      data.addAll(previousState.getData());
      return previousState.builder()
          .pullToRefreshLoading(false)
          .pullToRefreshError(null)
          .data(data)
          .build();
    }

    if (partialChanges instanceof PartialStateChanges.ProductsOfCategoryLoading) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialStateChanges.ProductsOfCategoryLoading) partialChanges).getCategoryName(),
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

    if (partialChanges instanceof PartialStateChanges.ProductsOfCategoryLoadingError) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialStateChanges.ProductsOfCategoryLoadingError) partialChanges).getCategoryName(),
          previousState.getData());

      AdditionalItemsLoadable foundItem = found.second;
      AdditionalItemsLoadable toInsert =
          new AdditionalItemsLoadable(foundItem.getMoreItemsAvailableCount(),
              foundItem.getCategoryName(), false,
              ((PartialStateChanges.ProductsOfCategoryLoadingError) partialChanges).getError());

      List<FeedItem> data = new ArrayList<>(previousState.getData().size());
      data.addAll(previousState.getData());
      data.set(found.first, toInsert);

      return previousState.builder().data(data).build();
    }

    if (partialChanges instanceof PartialStateChanges.ProductsOfCategoryLoaded) {
      Pair<Integer, AdditionalItemsLoadable> found = findAdditionalItems(
          ((PartialStateChanges.ProductsOfCategoryLoaded) partialChanges).getCategoryName(),
          previousState.getData());

      List<FeedItem> data = new ArrayList<>(previousState.getData().size()
          + ((PartialStateChanges.ProductsOfCategoryLoaded) partialChanges).getData().size());
      data.addAll(previousState.getData());

      // Search for the section header
      int sectionHeaderIndex = -1;
      for (int i = found.first; i >= 0; i--) {
        FeedItem item = previousState.getData().get(i);
        if (item instanceof SectionHeader && ((SectionHeader) item).getName()
            .equals(
                ((PartialStateChanges.ProductsOfCategoryLoaded) partialChanges).getCategoryName())) {
          sectionHeaderIndex = i;
          break;
        }

        // Remove all items of that category. The new list of products will be added afterwards
        data.remove(i);
      }

      if (sectionHeaderIndex < 0) {
        throw new RuntimeException("Couldn't find the section header for category "
            + ((PartialStateChanges.ProductsOfCategoryLoaded) partialChanges).getCategoryName());
      }

      data.addAll(sectionHeaderIndex + 1,
          ((PartialStateChanges.ProductsOfCategoryLoaded) partialChanges).getData());

      return previousState.builder().data(data).build();
    }

    throw new IllegalStateException("Don't know how to reduce the partial state " + partialChanges);
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
