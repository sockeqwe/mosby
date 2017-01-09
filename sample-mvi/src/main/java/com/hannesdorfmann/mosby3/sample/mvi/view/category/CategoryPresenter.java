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

package com.hannesdorfmann.mosby3.sample.mvi.view.category;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Hannes Dorfmann
 */

public class CategoryPresenter extends MviBasePresenter<CategoryView, CategoryViewState> {

  private final ProductBackendApiDecorator backendApi;

  public CategoryPresenter(ProductBackendApiDecorator backendApi) {
    this.backendApi = backendApi;
  }

  @Override protected void bindIntents() {
    Observable<CategoryViewState> categoryViewStateObservable =
        intent(CategoryView::loadIntents)
            .flatMap(categoryName -> backendApi.getAllProductsOfCategory(categoryName)
                .subscribeOn(Schedulers.io())
                .map(CategoryViewState.DataState::new)
                .cast(CategoryViewState.class)
                .startWith(new CategoryViewState.LoadingState())
                .onErrorReturn(CategoryViewState.ErrorState::new))
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(categoryViewStateObservable, CategoryView::render);
  }
}
