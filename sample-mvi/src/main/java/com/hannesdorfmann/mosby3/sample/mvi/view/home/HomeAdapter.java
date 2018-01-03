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

package com.hannesdorfmann.mosby3.sample.mvi.view.home;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.AdditionalItemsLoadable;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.SectionHeader;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.LoadingViewHolder;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.MoreItemsViewHolder;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.ProductViewHolder;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.SectionHederViewHolder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public class HomeAdapter extends RecyclerView.Adapter
    implements MoreItemsViewHolder.LoadItemsClickListener {

  static final int VIEW_TYPE_PRODUCT = 0;
  static final int VIEW_TYPE_LOADING_MORE_NEXT_PAGE = 1;
  static final int VIEW_TYPE_SECTION_HEADER = 2;
  static final int VIEW_TYPE_MORE_ITEMS_AVAILABLE = 3;

  private boolean isLoadingNextPage = false;
  private List<FeedItem> items;
  private final LayoutInflater layoutInflater;
  private final ProductViewHolder.ProductClickedListener productClickedListener;

  private PublishSubject<String> loadMoreItemsOfCategoryObservable = PublishSubject.create();

  public HomeAdapter(LayoutInflater layoutInflater,
      ProductViewHolder.ProductClickedListener productClickedListener) {
    this.layoutInflater = layoutInflater;
    this.productClickedListener = productClickedListener;
  }

  public List<FeedItem> getItems() {
    return items;
  }

  /**
   * @return true if value has changed since last invocation
   */
  public boolean setLoadingNextPage(boolean loadingNextPage) {
    boolean hasLoadingMoreChanged = loadingNextPage != isLoadingNextPage;

    boolean notifyInserted = loadingNextPage && hasLoadingMoreChanged;
    boolean notifyRemoved = !loadingNextPage && hasLoadingMoreChanged;
    isLoadingNextPage = loadingNextPage;

    if (notifyInserted) {
      notifyItemInserted(items.size());
    } else if (notifyRemoved) {
      notifyItemRemoved(items.size());
    }

    return hasLoadingMoreChanged;
  }

  public boolean isLoadingNextPage() {
    return isLoadingNextPage;
  }

  public void setItems(List<FeedItem> newItems) {
    List<FeedItem> oldItems = this.items;
    this.items = newItems;

    if (oldItems == null) {
      notifyDataSetChanged();
    } else {
      // Use Diff utils
      DiffUtil.calculateDiff(new DiffUtil.Callback() {
        @Override public int getOldListSize() {
          return oldItems.size();
        }

        @Override public int getNewListSize() {
          return newItems.size();
        }

        @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
          Object oldItem = oldItems.get(oldItemPosition);
          Object newItem = newItems.get(newItemPosition);

          if (oldItem instanceof Product
              && newItem instanceof Product
              && ((Product) oldItem).getId() == ((Product) newItem).getId()) {
            return true;
          }

          if (oldItem instanceof SectionHeader
              && newItem instanceof SectionHeader
              && ((SectionHeader) oldItem).getName().equals(((SectionHeader) newItem).getName())) {
            return true;
          }

          if (oldItem instanceof AdditionalItemsLoadable
              && newItem instanceof AdditionalItemsLoadable
              && ((AdditionalItemsLoadable) oldItem).getCategoryName()
              .equals(((AdditionalItemsLoadable) newItem).getCategoryName())) {
            return true;
          }

          return false;
        }

        @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
          Object oldItem = oldItems.get(oldItemPosition);
          Object newItem = newItems.get(newItemPosition);

          return oldItem.equals(newItem);
        }
      }, true).dispatchUpdatesTo(this);
    }
  }

  @Override public int getItemViewType(int position) {

    if (isLoadingNextPage && position == items.size()) {
      return VIEW_TYPE_LOADING_MORE_NEXT_PAGE;
    }

    FeedItem item = items.get(position);

    if (item instanceof Product) {
      return VIEW_TYPE_PRODUCT;
    } else if (item instanceof SectionHeader) {
      return VIEW_TYPE_SECTION_HEADER;
    } else if (item instanceof AdditionalItemsLoadable) {
      return VIEW_TYPE_MORE_ITEMS_AVAILABLE;
    }

    throw new IllegalArgumentException("Not able to dertermine the view type for item at position "
        + position
        + ". Item is: "
        + item);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_TYPE_PRODUCT:
        return ProductViewHolder.create(layoutInflater, productClickedListener);
      case VIEW_TYPE_LOADING_MORE_NEXT_PAGE:
        return LoadingViewHolder.create(layoutInflater);
      case VIEW_TYPE_MORE_ITEMS_AVAILABLE:
        return MoreItemsViewHolder.create(layoutInflater, this);
      case VIEW_TYPE_SECTION_HEADER:
        return SectionHederViewHolder.create(layoutInflater);
    }

    throw new IllegalArgumentException("Couldn't create a ViewHolder for viewType  = " + viewType);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    if (holder instanceof LoadingViewHolder) {
      return;
    }

    FeedItem item = items.get(position);
    if (holder instanceof ProductViewHolder) {
      ((ProductViewHolder) holder).bind((Product) item);
    } else if (holder instanceof SectionHederViewHolder) {
      ((SectionHederViewHolder) holder).onBind((SectionHeader) item);
    } else if (holder instanceof MoreItemsViewHolder) {
      ((MoreItemsViewHolder) holder).bind((AdditionalItemsLoadable) item);
    } else {
      throw new IllegalArgumentException("couldn't accept  ViewHolder " + holder);
    }
  }

  @Override public int getItemCount() {
    return items == null ? 0 : (items.size() + (isLoadingNextPage ? 1 : 0));
  }

  @Override public void loadItemsForCategory(String category) {
    loadMoreItemsOfCategoryObservable.onNext(category);
  }

  public Observable<String> loadMoreItemsOfCategoryObservable() {
    return loadMoreItemsOfCategoryObservable;
  }
}
