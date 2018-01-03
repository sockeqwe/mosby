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

package com.hannesdorfmann.mosby3.sample.mvi.view.search;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.interactor.search.SearchInteractor;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.interactor.search.SearchViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * MVI Presenter for {@link SearchView}
 *
 * @author Hannes Dorfmann
 */
public class SearchPresenter extends MviBasePresenter<SearchView, SearchViewState> {
  private final SearchInteractor searchInteractor;

  public SearchPresenter(SearchInteractor interactor) {
    super(new SearchViewState.SearchNotStartedYet());
    this.searchInteractor = interactor;
  }

  @Override protected void bindIntents() {
    Observable<SearchViewState> search =
        intent(SearchView::searchIntent)
            .doOnNext(s -> Timber.d("intent: Search '%s'", s))
            .switchMap(searchInteractor::search)
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(search, SearchView::render);
  }
}
