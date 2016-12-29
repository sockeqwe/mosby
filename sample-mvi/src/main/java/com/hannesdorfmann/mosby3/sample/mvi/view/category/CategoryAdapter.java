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

package com.hannesdorfmann.mosby3.sample.mvi.view.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.ProductViewHolder;
import java.util.List;

/**
 * Adapter display search results
 *
 * @author Hannes Dorfmann
 */
public class CategoryAdapter extends RecyclerView.Adapter<ProductViewHolder> {

  private final LayoutInflater inflater;
  private final ProductViewHolder.ProductClickedListener productClickedListener;
  private List<Product> products;

  public CategoryAdapter(LayoutInflater inflater, ProductViewHolder.ProductClickedListener productClickedListener) {
    this.inflater = inflater;
    this.productClickedListener = productClickedListener;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  @Override public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return ProductViewHolder.create(inflater, productClickedListener);
  }

  @Override public void onBindViewHolder(ProductViewHolder holder, int position) {
    holder.bind(products.get(position));
  }

  @Override public int getItemCount() {
    return products == null ? 0 : products.size();
  }
}
