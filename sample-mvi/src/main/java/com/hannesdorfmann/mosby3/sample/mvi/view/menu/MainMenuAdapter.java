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

package com.hannesdorfmann.mosby3.sample.mvi.view.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.MainMenuItem;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.MainMenuViewHolder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuViewHolder> implements MainMenuViewHolder.MainMenuSelectionListener{
  private final LayoutInflater layoutInflater;
  private List<MainMenuItem> items;
  private final PublishSubject<String> selectedItem = PublishSubject.create();
  public MainMenuAdapter(LayoutInflater layoutInflater) {
    this.layoutInflater = layoutInflater;
  }

  @Override public MainMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return MainMenuViewHolder.create(layoutInflater, this);
  }

  @Override public void onBindViewHolder(MainMenuViewHolder holder, int position) {
    holder.bind(items.get(position));
  }

  @Override public int getItemCount() {
    return items == null ? 0 : items.size();
  }

  @Override public void onItemSelected(String categoryName) {
    selectedItem.onNext(categoryName);
  }

  public Observable<String> getSelectedItemObservable() {
    return selectedItem;
  }

  public void setItems(List<MainMenuItem> items) {
    this.items = items;
  }
}
