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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;

/**
 * @author Hannes Dorfmann
 */
public class HomePresenter extends MviBasePresenter<HomeView, HomeViewState> {

  private final HomeFeedLoader feedLoader;

  public HomePresenter(HomeFeedLoader feedLoader) {
    this.feedLoader = feedLoader;
  }

  @Override public void attachView(HomeView view) {

    Observable<HomeViewState> loadFirstPage = intent(view.loadFirstPageIntent()).flatMap(
        ignored -> feedLoader.loadFirstPage()
            .map(items -> (HomeViewState) new HomeViewState.DataState(items, false, null, false,
                null))
            .startWith(new HomeViewState.FirstPageLoadingState())
            .onErrorReturn(HomeViewState.FirstPageErrorState::new)
            .subscribeOn(Schedulers.io()));

    Observable<HomeViewState> nextPage = intent(view.loadNextPageIntent()).flatMap(
        ignored -> feedLoader.loadNextPage()
            .map(items -> new HomeViewState.DataState(items, false, null, false, null))
            .startWith(
                new HomeViewState.DataState(Collections.emptyList(), true, null, false, null))
            .onErrorReturn(
                error -> new HomeViewState.DataState(Collections.emptyList(), false, error, false,
                    null))
            .subscribeOn(Schedulers.io()));

    Observable<HomeViewState> pullToRefreshObservable = intent(view.pullToRefreshIntent()).flatMap(
        ignored -> feedLoader.loadNewestPage().subscribeOn(Schedulers.io()))
        .map(items -> (HomeViewState) new HomeViewState.DataState(items, false, null, false, null))
        .startWith(new HomeViewState.DataState(Collections.emptyList(), false, null, true, null))
        .onErrorReturn(
            error -> new HomeViewState.DataState(Collections.emptyList(), false, null, false,
                error));

    Observable<HomeViewState> allIntents =
        Observable.merge(loadFirstPage, nextPage, pullToRefreshObservable)
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(new HomeViewState.FirstPageLoadingState(), allIntents, view::render);
  }
}
