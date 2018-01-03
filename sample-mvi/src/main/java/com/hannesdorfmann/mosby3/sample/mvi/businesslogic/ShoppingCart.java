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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds a list of items that has been added to the shopping cart
 *
 * @author Hannes Dorfmann
 */
public class ShoppingCart {
  private BehaviorSubject<List<Product>> itemsInShoppingCart =
      BehaviorSubject.createDefault(Collections.emptyList());

  /**
   * An observable to observe the items in the shopping cart
   */
  public Observable<List<Product>> itemsInShoppingCart() {
    return itemsInShoppingCart;
  }

  /**
   * Adds a product to the shopping cart
   */
  public Completable addProduct(Product product) {
    List<Product> updatedShoppingCart = new ArrayList<>();
    updatedShoppingCart.addAll(itemsInShoppingCart.getValue());
    updatedShoppingCart.add(product);
    itemsInShoppingCart.onNext(updatedShoppingCart);
    return Completable.complete();
  }

  /**
   * Remove a product to the shopping cart
   */
  public Completable removeProduct(Product product) {
    List<Product> updatedShoppingCart = new ArrayList<>();
    updatedShoppingCart.addAll(itemsInShoppingCart.getValue());
    updatedShoppingCart.remove(product);
    itemsInShoppingCart.onNext(updatedShoppingCart);
    return Completable.complete();
  }

  /**
   * Remove a list of Products from the shopping cart
   */
  public Completable removeProducts(List<Product> products) {
    List<Product> updatedShoppingCart = new ArrayList<>();
    updatedShoppingCart.addAll(itemsInShoppingCart.getValue());
    updatedShoppingCart.removeAll(products);
    itemsInShoppingCart.onNext(updatedShoppingCart);
    return Completable.complete();
  }
}
