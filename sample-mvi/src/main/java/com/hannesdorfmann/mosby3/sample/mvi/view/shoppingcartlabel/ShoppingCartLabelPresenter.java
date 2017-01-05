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

package com.hannesdorfmann.mosby3.sample.mvi.view.shoppingcartlabel;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.ShoppingCart;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.List;
import timber.log.Timber;

/**
 * Presenter for  {@link ShoppingCartLabelView} that displays the number of items in the shopping
 * cart
 *
 * @author Hannes Dorfmann
 */
public class ShoppingCartLabelPresenter extends MviBasePresenter<ShoppingCartLabelView, Integer> {

  private final ShoppingCart shoppingCart;

  public ShoppingCartLabelPresenter(ShoppingCart shoppingCart) {
    this.shoppingCart = shoppingCart;
  }

  @Override protected void bindIntents() {
    Observable<Integer> numberOfItemsInShoppingCart =
        intent(ShoppingCartLabelView::loadIntent).doOnNext(
            ignored -> Timber.d("intent: load number of items in shopping cart"))
            .flatMap(ignored -> shoppingCart.itemsInShoppingCart())
            .map(List::size)
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(numberOfItemsInShoppingCart, ShoppingCartLabelView::render);
  }
}
