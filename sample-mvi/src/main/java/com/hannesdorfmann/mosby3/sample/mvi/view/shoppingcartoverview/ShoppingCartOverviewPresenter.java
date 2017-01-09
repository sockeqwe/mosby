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

package com.hannesdorfmann.mosby3.sample.mvi.view.shoppingcartoverview;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.ShoppingCart;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */
public class ShoppingCartOverviewPresenter
    extends MviBasePresenter<ShoppingCartOverviewView, List<ShoppingCartOverviewItem>> {

  private final ShoppingCart shoppingCart;
  private final Observable<Boolean> deleteSelectedItemsIntent;
  private final Observable<Boolean> clearSelectionIntent;
  private Disposable deleteDisposable;
  private Disposable deleteSelectedDisposable;

  public ShoppingCartOverviewPresenter(ShoppingCart shoppingCart,
      Observable<Boolean> deleteSelectedItemsIntent, Observable<Boolean> clearSelectionIntent) {
    this.shoppingCart = shoppingCart;
    this.deleteSelectedItemsIntent = deleteSelectedItemsIntent;
    this.clearSelectionIntent = clearSelectionIntent;
  }

  @Override protected void bindIntents() {

    //
    // Observable that emits a list of selected products over time (or empty list if the selection has been cleared)
    //
    Observable<List<Product>> selectedItemsIntent =
        intent(ShoppingCartOverviewView::selectItemsIntent)
            .mergeWith(clearSelectionIntent.map(ignore -> Collections.emptyList()))
            .doOnNext(items -> Timber.d("intent: selected items %d", items.size()))
            .startWith(new ArrayList<Product>(0));

    //
    // Delete multiple selected Items
    //

    deleteSelectedDisposable = selectedItemsIntent
        .switchMap(selectedItems -> deleteSelectedItemsIntent.filter(ignored -> !selectedItems.isEmpty())
            .doOnNext(ignored -> Timber.d("intent: remove %d selected items from shopping cart", selectedItems.size()))
            .flatMap(ignore -> shoppingCart.removeProducts(selectedItems).toObservable()))
        .subscribe();

    //
    // Delete a single item
    //
    deleteDisposable = intent(ShoppingCartOverviewView::removeItemIntent)
        .doOnNext(item -> Timber.d("intent: remove item from shopping cart: %s", item))
        .flatMap(productToDelete -> shoppingCart.removeProduct(productToDelete).toObservable())
        .subscribe();
    //
    // Display a list of items in the shopping cart
    //
    Observable<List<Product>> shoppingCartContentObservable =
        intent(ShoppingCartOverviewView::loadItemsIntent)
            .doOnNext(ignored -> Timber.d("load ShoppingCart intent"))
            .flatMap(ignored -> shoppingCart.itemsInShoppingCart());


    //
    // Display list of items / view state
    //
    List<Observable<?>> combiningObservables =
        Arrays.asList(shoppingCartContentObservable, selectedItemsIntent);

    Observable<List<ShoppingCartOverviewItem>> shoppingCartContentWithSelectedItems =
        Observable.combineLatest(combiningObservables, results -> {
          List<Product> itemsInShoppingCart = (List<Product>) results[0];
          List<Product> selectedProducts = (List<Product>) results[1];

          List<ShoppingCartOverviewItem> items = new ArrayList<>(itemsInShoppingCart.size());
          for (int i = 0; i < itemsInShoppingCart.size(); i++) {
            Product p = itemsInShoppingCart.get(i);
            items.add(new ShoppingCartOverviewItem(p, selectedProducts.contains(p)));
          }
          return items;
        })
        .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(shoppingCartContentWithSelectedItems, ShoppingCartOverviewView::render);
  }

  @Override protected void unbindIntents() {
    deleteDisposable.dispose();
    deleteSelectedDisposable.dispose();
  }
}
