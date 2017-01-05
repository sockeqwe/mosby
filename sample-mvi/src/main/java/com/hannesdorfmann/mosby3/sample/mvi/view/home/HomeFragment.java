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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class HomeFragment extends MviFragment<HomeView, HomePresenter>
    implements HomeView, ProductViewHolder.ProductClickedListener {

  private HomeAdapter adapter;
  private GridLayoutManager layoutManager;
  private Unbinder unbinder;

  @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.errorView) TextView errorView;
  @BindInt(R.integer.grid_span_size) int spanCount;

  @NonNull @Override public HomePresenter createPresenter() {
    Timber.d("createPresenter");
    return SampleApplication.getDependencyInjection(getActivity()).newHomePresenter();
  }

  @Override public void onProductClicked(Product product) {
    ProductDetailsActivity.start(getActivity(), product);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    unbinder = ButterKnife.bind(this, view);

    adapter = new HomeAdapter(inflater, this);
    layoutManager = new GridLayoutManager(getActivity(), spanCount);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {

        int viewType = adapter.getItemViewType(position);
        if (viewType == HomeAdapter.VIEW_TYPE_LOADING_MORE_NEXT_PAGE
            || viewType == HomeAdapter.VIEW_TYPE_SECTION_HEADER) {
          return spanCount;
        }

        return 1;
      }
    });

    recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount,
        getResources().getDimensionPixelSize(R.dimen.grid_spacing), true));

    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);

    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public Observable<Boolean> loadFirstPageIntent() {
    return Observable.just(true).doOnComplete(() -> Timber.d("firstPage completed"));
  }

  @Override public Observable<Boolean> loadNextPageIntent() {
    return RxJavaInterop.toV2Observable(RxRecyclerView.scrollStateChanges(recyclerView))
        .filter(event -> !adapter.isLoadingNextPage())
        .filter(event -> event == RecyclerView.SCROLL_STATE_IDLE)
        .filter(event -> layoutManager.findLastCompletelyVisibleItemPosition()
            == adapter.getItems().size() - 1)
        .map(integer -> true);
  }

  @Override public Observable<Boolean> pullToRefreshIntent() {
    return RxJavaInterop.toV2Observable(
        RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).map(ignored -> true));
  }

  @Override public Observable<String> loadAllProductsFromCategoryIntent() {
    return adapter.loadMoreItemsOfCategoryObservable();
  }

  @Override public void render(HomeViewState viewState) {
    Timber.d("render %s", viewState);
    if (!viewState.isLoadingFirstPage() && viewState.getFirstPageError() == null) {
      renderShowData(viewState);
    } else if (viewState.isLoadingFirstPage()) {
      renderFirstPageLoading();
    } else if (viewState.getFirstPageError() != null) {
      renderFirstPageError();
    } else {
      throw new IllegalStateException("Unknown view state " + viewState);
    }
  }

  private void renderShowData(HomeViewState state) {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
    boolean changed = adapter.setLoadingNextPage(state.isLoadingNextPage());
    if (changed && state.isLoadingNextPage()) {
      // scroll to the end of the list so that the user sees the load more progress bar
      recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }
    adapter.setItems(state.getData());

    boolean pullToRefreshFinished = swipeRefreshLayout.isRefreshing()
        && !state.isLoadingPullToRefresh()
        && state.getPullToRefreshError() == null;
    if (pullToRefreshFinished) {
      // Swipe to refresh finished successfully so scroll to the top of the list (to see inserted items)
      recyclerView.smoothScrollToPosition(0);
    }

    swipeRefreshLayout.setRefreshing(state.isLoadingPullToRefresh());

    if (state.getNextPageError() != null) {
      Snackbar.make(getView(), R.string.error_unknown, Snackbar.LENGTH_LONG)
          .show(); // TODO callback
    }

    if (state.getPullToRefreshError() != null) {
      Snackbar.make(getView(), R.string.error_unknown, Snackbar.LENGTH_LONG)
          .show(); // TODO callback
    }
  }

  private void renderFirstPageLoading() {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.GONE);
  }

  private void renderFirstPageError() {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.VISIBLE);
  }
}
