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

package com.hannesdorfmann.mosby3.sample.mvi.view.shoppingcartlist;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.ShoppingCart;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class ShoppingCartPresenter
    extends MviBasePresenter<ShoppingCartView, List<ShoppingCartItem>> {

  private final ShoppingCart shoppingCart;
  private final Observable<Boolean> deleteSelectedItemsIntent;

  public ShoppingCartPresenter(ShoppingCart shoppingCart,
      Observable<Boolean> deleteSelectedItemsIntent) {
    this.shoppingCart = shoppingCart;
    this.deleteSelectedItemsIntent = deleteSelectedItemsIntent;
  }

  @Override protected void bindIntents() {

    Observable<List<Product>> selectedItemsIntent =
        intent(ShoppingCartView::selectItemsIntent).startWith(new ArrayList<Product>(0));

    //
    // Delete Items
    //

    /*
    selectedItemsIntent.filter(items -> !items.isEmpty())
        .concatWith(deleteSelectedItemsIntent)
        .switchMap(selectedItems -> shoppingCart.removeProducts(selectedItems).toObservable())
        .subscribe();
*/

    //
    // Display a list of items in the shopping cart
    //
    Observable<List<Product>> shoppingCartContentObservable =
        intent(ShoppingCartView::loadItemsIntent).doOnNext(
            ignored -> Timber.d("load ShoppingCart intent"))
            .flatMap(ignored -> shoppingCart.itemsInShoppingCart());
    List<Observable<?>> combiningObservables =
        Arrays.asList(shoppingCartContentObservable, selectedItemsIntent);

    Observable<List<ShoppingCartItem>> shoppingCartContentWithSelectedItems =
        Observable.combineLatest(combiningObservables, results -> {
          List<Product> itemsInShoppingCart = (List<Product>) results[0];
          List<Product> selectedProducts = (List<Product>) results[1];

          List<ShoppingCartItem> items =
              new ArrayList<ShoppingCartItem>(itemsInShoppingCart.size());
          for (int i = 0; i < itemsInShoppingCart.size(); i++) {
            Product p = itemsInShoppingCart.get(i);
            items.add(new ShoppingCartItem(p, selectedProducts.contains(p)));
          }
          return items;
        });

    subscribeViewState(shoppingCartContentWithSelectedItems, ShoppingCartView::render);
  }
}
