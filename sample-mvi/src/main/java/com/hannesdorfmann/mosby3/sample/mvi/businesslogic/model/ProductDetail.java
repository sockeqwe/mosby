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

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model;

/**
 * @author Hannes Dorfmann
 */
public class ProductDetail {

  private final Product product;
  private final boolean isInShoppingCart;

  public ProductDetail(Product product, boolean isInShoppingCart) {
    this.product = product;
    this.isInShoppingCart = isInShoppingCart;
  }

  public Product getProduct() {
    return product;
  }

  public boolean isInShoppingCart() {
    return isInShoppingCart;
  }

  @Override public String toString() {
    return "ProductDetail{" +
        "product=" + product +
        ", isInShoppingCart=" + isInShoppingCart +
        '}';
  }
}
