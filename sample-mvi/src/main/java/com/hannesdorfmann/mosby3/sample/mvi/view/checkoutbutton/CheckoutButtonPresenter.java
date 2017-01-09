/*
 * Copyright 2017 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.sample.mvi.view.checkoutbutton;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.ShoppingCart;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Presenter for  {@link CheckoutButtonView} that displays the sum of all items shopping
 * cart
 *
 * @author Hannes Dorfmann
 */
public class CheckoutButtonPresenter extends MviBasePresenter<CheckoutButtonView, Double> {

  private final ShoppingCart shoppingCart;

  public CheckoutButtonPresenter(ShoppingCart shoppingCart) {
    this.shoppingCart = shoppingCart;
  }

  @Override protected void bindIntents() {
    Observable<Double> numberOfItemsInShoppingCart =
        intent(CheckoutButtonView::loadIntent)
            .doOnNext(ignored -> Timber.d("intent: load number of items in shopping cart"))
            .flatMap(ignored -> shoppingCart.itemsInShoppingCart())
            .map(items -> {
              double sum = 0;
              for (int i = 0; i < items.size(); i++) {
                sum += items.get(i).getPrice();
              }
              return sum;
            })
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(numberOfItemsInShoppingCart, CheckoutButtonView::render);
  }
}
