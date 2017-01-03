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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hannesdorfmann.mosby3.mvi.MviFragment;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import java.util.List;
import timber.log.Timber;

/**
 * This class doesn't neccessarily has to be a fragment. It's just a fragment because I want to
 * demonstrate that mosby works with fragments in xml layouts too.
 *
 * @author Hannes Dorfmann
 */
public class ShoppingCartFragment extends MviFragment<ShoppingCartView, ShoppingCartPresenter>
    implements ShoppingCartView {

  private ShoppingCartAdapter adapter;
  private Unbinder unbinder;
  @BindView(R.id.shoppingCartRecyclerView) RecyclerView recyclerView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    unbinder = ButterKnife.bind(this, v);
    adapter = new ShoppingCartAdapter(getActivity());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    return v;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @NonNull @Override public ShoppingCartPresenter createPresenter() {
    Timber.d("Create Presenter");
    return SampleApplication.getDependencyInjection(getActivity()).getShoppingCartPresenter();
  }

  @Override public Observable<Boolean> loadItemsIntent() {
    return Observable.just(true);
  }

  @Override public Observable<List<Product>> selectItemsIntent() {
    return adapter.selectedItemsObservable();
  }

  @Override public Observable<Product> removeItemIntent() {
    return null;
  }

  @Override public void render(List<ShoppingCartItem> itemsInShoppingCart) {
    adapter.setItems(itemsInShoppingCart);
  }
}
