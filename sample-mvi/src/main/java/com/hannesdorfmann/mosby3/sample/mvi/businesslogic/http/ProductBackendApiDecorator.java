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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Since this app only has a static backend providing some static json responses,
 * we have to calculate some things locally on the app users device, that otherwise would be done
 * on
 * a real backend server.
 *
 * All app components should interact with this decorator class and not with the real retrofit
 * interface.
 *
 * @author Hannes Dorfmann
 */
public class ProductBackendApiDecorator {
  private final ProductBackendApi api;

  public ProductBackendApiDecorator(ProductBackendApi api) {
    this.api = api;
  }

  public Observable<List<Product>> getProducts(int pagination) {
    return api.getProducts(pagination);
  }

  /**
   * Get a list with all products from backend
   */
  public Observable<List<Product>> getAllProducts() {
    return Observable.zip(getProducts(0), getProducts(1), getProducts(2), getProducts(3),
        (products0, products1, products2, products3) -> {
          List<Product> productList = new ArrayList<Product>();
          productList.addAll(products0);
          productList.addAll(products1);
          productList.addAll(products2);
          productList.addAll(products3);
          return productList;
        });
  }

  /**
   * Get all products of a certain category
   *
   * @param categoryName The name of the category
   */
  public Observable<List<Product>> getAllProductsOfCategory(String categoryName) {
    return getAllProducts().flatMap(Observable::fromIterable)
        .filter(product -> product.getCategory().equals(categoryName))
        .toList()
        .toObservable();
  }

  /**
   * Get a list with all categories
   */
  public Observable<List<String>> getAllCategories() {
    return getAllProducts().map(products -> {
      Set<String> categories = new HashSet<String>();
      for (Product p : products) {
        categories.add(p.getCategory());
      }

      List<String> result = new ArrayList<String>(categories.size());
      result.addAll(categories);
      return result;
    });
  }

  /**
   * Get the product with the given id
   *
   * @param productId The product id
   */
  public Observable<Product> getProduct(int productId) {
    return getAllProducts().flatMap(products -> Observable.fromIterable(products))
        .filter(product -> product.getId() == productId)
        .take(1);
  }
}
