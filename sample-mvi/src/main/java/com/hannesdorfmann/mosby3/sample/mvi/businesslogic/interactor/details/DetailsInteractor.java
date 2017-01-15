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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.interactor.details;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.ShoppingCart;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.ProductDetail;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;

/**
 * Interactor that is responsible to load product details
 *
 * @author Hannes Dorfmann
 */
public class DetailsInteractor {
  private final ProductBackendApiDecorator backendApi;
  private final ShoppingCart shoppingCart;

  public DetailsInteractor(ProductBackendApiDecorator backendApi, ShoppingCart shoppingCart) {
    this.backendApi = backendApi;
    this.shoppingCart = shoppingCart;
  }


  private Observable<ProductDetail> getProductWithShoppingCartInfo(int productId) {
    List<Observable<?>> observables =
        Arrays.asList(backendApi.getProduct(productId), shoppingCart.itemsInShoppingCart());

    return Observable.combineLatest(observables, objects -> {
      Product product = (Product) objects[0];
      List<Product> productsInShoppingCart = (List<Product>) objects[1];
      boolean inShoppingCart = false;
      for (Product p : productsInShoppingCart) {
        if (p.getId() == productId) {
          inShoppingCart = true;
          break;
        }
      }

      return new ProductDetail(product, inShoppingCart);
    });
  }

  /**
   * Get the details of a given product
   */
  public Observable<ProductDetailsViewState> getDetails(int productId) {
    return getProductWithShoppingCartInfo(productId)
        .subscribeOn(Schedulers.io())
        .map(ProductDetailsViewState.DataState::new)
        .cast(ProductDetailsViewState.class)
        .startWith(new ProductDetailsViewState.LoadingState())
        .onErrorReturn(ProductDetailsViewState.ErrorState::new);
  }

  public Completable addToShoppingCart(Product product) {
    return shoppingCart.addProduct(product);
  }

  public Completable removeFromShoppingCart(Product product) {
    return shoppingCart.removeProduct(product);
  }
}
