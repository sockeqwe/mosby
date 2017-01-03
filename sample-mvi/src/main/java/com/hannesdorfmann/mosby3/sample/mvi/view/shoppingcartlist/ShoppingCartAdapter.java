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

import android.app.Activity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.view.detail.ProductDetailsActivity;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.ShoppingCartItemViewHolder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartItemViewHolder>
    implements ShoppingCartItemViewHolder.ItemSelectedListener {

  private final LayoutInflater layoutInflater;
  private final Activity activity;
  private List<ShoppingCartItem> items;
  private PublishSubject<List<Product>> selectedProducts = PublishSubject.create();

  public ShoppingCartAdapter(Activity activity) {
    this.activity = activity;
    this.layoutInflater = activity.getLayoutInflater();
  }

  @Override public ShoppingCartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return ShoppingCartItemViewHolder.create(layoutInflater, this);
  }

  @Override public void onBindViewHolder(ShoppingCartItemViewHolder holder, int position) {
    holder.bind(items.get(position));
  }

  @Override public int getItemCount() {
    return items == null ? 0 : items.size();
  }

  public boolean isInSelectionMode() {
    for (ShoppingCartItem item : items) {
      if (item.isSelected()) return true;
    }

    return false;
  }

  @Override public void onItemClicked(ShoppingCartItem product) {
    if (isInSelectionMode()) {
      toggleSelection(product);
    } else {
      ProductDetailsActivity.start(activity, product.getProduct());
    }
  }

  @Override public boolean onItemLongPressed(ShoppingCartItem product) {

    toggleSelection(product);
    return true;
  }

  public void setItems(List<ShoppingCartItem> items) {
    List<ShoppingCartItem> beforeItems = this.items;
    this.items = items;
    if (beforeItems == null) {
      notifyDataSetChanged();
    } else {
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
        @Override public int getOldListSize() {
          return beforeItems.size();
        }

        @Override public int getNewListSize() {
          return items.size();
        }

        @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
          return beforeItems.get(oldItemPosition)
              .getProduct()
              .equals(items.get(newItemPosition).getProduct());
        }

        @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
          return false;
        }
      });
      diffResult.dispatchUpdatesTo(this);
    }
  }

  private void toggleSelection(ShoppingCartItem toToggle) {
    List<Product> selectedItems = new ArrayList<>();
    for (ShoppingCartItem item : items) {

      if (item == toToggle && !toToggle.isSelected()) {
        selectedItems.add(item.getProduct());
      } else if (item.isSelected()) {
        selectedItems.add(item.getProduct());
      }
    }

    selectedProducts.onNext(selectedItems);
  }

  public Observable<List<Product>> selectedItemsObservable() {
    return selectedProducts;
  }
}
