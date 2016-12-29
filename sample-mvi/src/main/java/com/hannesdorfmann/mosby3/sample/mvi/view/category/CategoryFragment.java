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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hannesdorfmann.mosby3.mvi.MviFragment;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.view.detail.ProductDetailsActivity;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.GridSpacingItemDecoration;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.viewholder.ProductViewHolder;
import io.reactivex.Observable;
import java.util.List;
import timber.log.Timber;

/**
 * Displays all products of a certain category on the screen
 *
 * @author Hannes Dorfmann
 */
public class CategoryFragment extends MviFragment<CategoryView, CategoryPresenter>
    implements CategoryView, ProductViewHolder.ProductClickedListener {

  private final static String CATEGORY_NAME = "categoryName";

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.errorView) View errorView;
  @BindInt(R.integer.grid_span_size) int spanCount;
  private Unbinder unbinder;
  private CategoryAdapter adapter;

  @Override public void onProductClicked(Product product) {
    ProductDetailsActivity.start(getActivity(), product);
  }

  @NonNull public static CategoryFragment newInstance(@NonNull String categoryName) {
    if (categoryName == null) {
      throw new NullPointerException("category name == null");
    }
    CategoryFragment fragment = new CategoryFragment();
    Bundle args = new Bundle();
    args.putString(CATEGORY_NAME, categoryName);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category, container, false);
    unbinder = ButterKnife.bind(this, view);
    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
    adapter = new CategoryAdapter(inflater, this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount,
        getResources().getDimensionPixelSize(R.dimen.grid_spacing), true));
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @NonNull @Override public CategoryPresenter createPresenter() {
    Timber.d("Create presenter");
    return SampleApplication.getDependencyInjection(getContext()).newCategoryPresenter();
  }

  @Override public Observable<String> loadIntents() {
    return Observable.just(getArguments().getString(CATEGORY_NAME));
  }

  @Override public void render(CategoryViewState state) {
    Timber.d("Render %s", state);
    if (state instanceof CategoryViewState.LoadingState) {
      renderLoading();
    } else if (state instanceof CategoryViewState.DataState) {
      renderData(((CategoryViewState.DataState) state).getProducts());
    }
    if (state instanceof CategoryViewState.ErrorState) {
      renderError();
    }
  }

  private void renderError() {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
  }

  private void renderLoading() {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
  }

  private void renderData(List<Product> products) {
    adapter.setProducts(products);
    adapter.notifyDataSetChanged();
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
  }
}
