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

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.feed.HomeFeedLoader;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class HomePresenter extends MviBasePresenter<HomeView, HomeViewState> {

  private final HomeFeedLoader feedLoader;

  public HomePresenter(HomeFeedLoader feedLoader) {
    this.feedLoader = feedLoader;
  }

  @Override public void attachView(HomeView view) {

    Observable<PartialHomeViewState> loadFirstPage = intent(view.loadFirstPageIntent()).flatMap(
        ignored -> feedLoader.loadFirstPage()
            .map(items -> (PartialHomeViewState) new PartialHomeViewState.FirstPageLoaded(items))
            .startWith(new PartialHomeViewState.FirstPageLoading())
            .onErrorReturn(PartialHomeViewState.FirstPageError::new)
            .subscribeOn(Schedulers.io()));

    Observable<PartialHomeViewState> nextPage = intent(view.loadNextPageIntent()).flatMap(
        ignored -> feedLoader.loadNextPage()
            .map(items -> (PartialHomeViewState) new PartialHomeViewState.NextPageLoaded(items))
            .startWith(new PartialHomeViewState.NextPageLoading())
            .onErrorReturn(PartialHomeViewState.NexPageLoadingError::new)
            .subscribeOn(Schedulers.io()));

    Observable<PartialHomeViewState> pullToRefreshObservable =
        intent(view.pullToRefreshIntent()).flatMap(
            ignored -> feedLoader.loadNewestPage().subscribeOn(Schedulers.io()))
            .map(
                items -> (PartialHomeViewState) new PartialHomeViewState.PullToRefreshLoaded(items))
            .startWith(new PartialHomeViewState.PullToRefreshLoading())
            .onErrorReturn(PartialHomeViewState.PullToRefeshLoadingError::new)
            .subscribeOn(Schedulers.io());

    Observable<PartialHomeViewState> partialViewStateFromIntentsObservable =
        Observable.merge(loadFirstPage, nextPage, pullToRefreshObservable)
            .observeOn(AndroidSchedulers.mainThread());

    HomeViewState initialViewState = new HomeViewState.Builder().firstPageLoading(true).build();
    subscribeViewState(initialViewState,
        partialViewStateFromIntentsObservable.scan(initialViewState, this::viewStateReducer),
        view::render);
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
      data.addAll(0, ((PartialHomeViewState.PullToRefreshLoaded) newState).getData());

      return previousState.builder()
          .pullToRefreshLoading(true)
          .pullToRefreshError(null)
          .data(data)
          .build();
    }

    throw new IllegalStateException("Don't know how to reduce the partial state " + newState);
  }
}
