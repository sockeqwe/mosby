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
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.AdditionalItemsLoadable;

/**
 * @author Hannes Dorfmann
 */
public class MoreItemsViewHolder extends RecyclerView.ViewHolder {

  public interface LoadItemsClickListener {
    public void loadItemsForCategory(String category);
  }

  public static MoreItemsViewHolder create(LayoutInflater layoutInflater,
      LoadItemsClickListener clickListener) {
    return new MoreItemsViewHolder(
        layoutInflater.inflate(R.layout.item_more_available, null, false), clickListener);
  }

  @BindView(R.id.moreItemsCount) TextView moreItemsCount;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.loadMoreButtton) View loadMoreButton;
  @BindView(R.id.errorRetryButton) Button errorRetry;

  private AdditionalItemsLoadable currentItem;

  private MoreItemsViewHolder(View itemView, LoadItemsClickListener listener) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    itemView.setOnClickListener(v -> listener.loadItemsForCategory(currentItem.getCategoryName()));
    errorRetry.setOnClickListener(
        v -> listener.loadItemsForCategory(currentItem.getCategoryName()));
    loadMoreButton.setOnClickListener(
        v -> listener.loadItemsForCategory(currentItem.getCategoryName()));
  }

  public void bind(AdditionalItemsLoadable item) {
    this.currentItem = item;

    if (item.isLoading()) {
      // TransitionManager.beginDelayedTransition((ViewGroup) itemView);
      moreItemsCount.setVisibility(View.GONE);
      loadMoreButton.setVisibility(View.GONE);
      loadingView.setVisibility(View.VISIBLE);
      errorRetry.setVisibility(View.GONE);
      itemView.setClickable(false);
    } else if (item.getLoadingError() != null) {
      //TransitionManager.beginDelayedTransition((ViewGroup) itemView);
      moreItemsCount.setVisibility(View.GONE);
      loadMoreButton.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
      errorRetry.setVisibility(View.VISIBLE);
      itemView.setClickable(true);
    } else {
      moreItemsCount.setText("+" + item.getMoreItemsCount());
      moreItemsCount.setVisibility(View.VISIBLE);
      loadMoreButton.setVisibility(View.VISIBLE);
      loadingView.setVisibility(View.GONE);
      errorRetry.setVisibility(View.GONE);
      itemView.setClickable(true);
    }
  }
}
