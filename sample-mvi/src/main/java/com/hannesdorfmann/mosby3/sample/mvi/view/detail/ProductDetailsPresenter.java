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

package com.hannesdorfmann.mosby3.sample.mvi.view.detail;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.DetailsInteractor;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class ProductDetailsPresenter
    extends MviBasePresenter<ProductDetailsView, ProductDetailsViewState> {

  private final DetailsInteractor interactor;

  public ProductDetailsPresenter(DetailsInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void bindIntents() {

    intent(ProductDetailsView::addToShoppingCartIntent)
        .doOnNext(product -> Timber.d("intent: add to shopping cart %s", product))
        .flatMap(product -> interactor.addToShoppingCart(product).toObservable()).subscribe();

    intent(ProductDetailsView::removeFromShoppingCartIntent)
        .doOnNext(product -> Timber.d("intent: remove from shopping cart %s", product))
        .flatMap(product -> interactor.removeFromShoppingCart(product).toObservable())
        .subscribe();

    Observable<ProductDetailsViewState> loadDetails =
        intent(ProductDetailsView::loadDetailsIntent)
            .doOnNext(productId -> Timber.d("intent: load details for product id = %s", productId))
            .flatMap(productId -> interactor.getDetails(productId)
                .subscribeOn(Schedulers.io())
                .map(ProductDetailsViewState.DataState::new)
                .cast(ProductDetailsViewState.class)
                .startWith(new ProductDetailsViewState.LoadingState())
                .onErrorReturn(ProductDetailsViewState.ErrorState::new))
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(loadDetails, ProductDetailsView::render);
  }
}
