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

import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;

/**
 * Is part of the view state for {@link ShoppingCartOverviewView}
 *
 * @author Hannes Dorfmann
 */
public class ShoppingCartOverviewItem {
  private final Product product;
  private final boolean isSelected;

  public ShoppingCartOverviewItem(@NonNull Product product, boolean isSelected) {
    this.product = product;
    this.isSelected = isSelected;
  }

  @NonNull public Product getProduct() {
    return product;
  }

  public boolean isSelected() {
    return isSelected;
  }

  @Override public String toString() {
    return "ShoppingCartItem{" +
        "product=" + product +
        ", isSelected=" + isSelected +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ShoppingCartOverviewItem item = (ShoppingCartOverviewItem) o;

    if (isSelected != item.isSelected) return false;
    return product.equals(item.product);
  }

  @Override public int hashCode() {
    int result = product.hashCode();
    result = 31 * result + (isSelected ? 1 : 0);
    return result;
  }
}
