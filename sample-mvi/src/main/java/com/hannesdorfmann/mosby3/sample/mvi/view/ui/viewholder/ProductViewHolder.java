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

package com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApi;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;

/**
 * View Holder just to display
 *
 * @author Hannes Dorfmann
 */
public class ProductViewHolder extends RecyclerView.ViewHolder {

  public interface ProductClickedListener {
    public void onProductClicked(Product product);
  }

  public static ProductViewHolder create(LayoutInflater inflater, ProductClickedListener listener) {
    return new ProductViewHolder(inflater.inflate(R.layout.item_product, null, false), listener);
  }

  @BindView(R.id.productImage) ImageView image;
  @BindView(R.id.productName) TextView name;

  private Product product;

  private ProductViewHolder(View itemView, ProductClickedListener clickedListener) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    itemView.setOnClickListener(v -> clickedListener.onProductClicked(product));
  }

  public void bind(Product product) {
    this.product = product;
    Glide.with(itemView.getContext())
        .load(ProductBackendApi.BASE_IMAGE_URL + product.getImage())
        .centerCrop()
        .into(image);
    name.setText(product.getName());
  }
}
