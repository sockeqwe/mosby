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
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.GridSpacingItemDecoration;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class HomeFragment extends MviFragment<HomeView, HomePresenter> implements HomeView {

  private HomeAdapter adapter;
  private GridLayoutManager layoutManager;
  private Unbinder unbinder;
  private PublishSubject<Boolean> startLoadingObservable = PublishSubject.create();

  @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.errorView) TextView errorView;
  @BindInt(R.integer.grid_span_size) int spanSize;

  @NonNull @Override public HomePresenter createPresenter() {
    Timber.d("createPresenter");
    return SampleApplication.getDependencyInjection(getActivity()).newHomePresenter();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    unbinder = ButterKnife.bind(this, view);

    adapter = new HomeAdapter(inflater);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanSize,
        getResources().getDimensionPixelSize(R.dimen.grid_spacing), true));
    layoutManager = new GridLayoutManager(getActivity(), 2);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {

        int viewType = adapter.getItemViewType(position);
        if (viewType == HomeAdapter.VIEW_TYPE_LOADING_MORE_NEXT_PAGE
            || viewType == HomeAdapter.VIEW_TYPE_SECTION_HEADER) {
          return 2;
        }

        return 1;
      }
    });
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);

    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onStart() {
    super.onStart();
    startLoadingObservable.onNext(true);
    startLoadingObservable.onComplete(); // Never fire again after orientation change
  }

  @Override public Observable<Boolean> loadFirstPageIntent() {
    return startLoadingObservable.doOnNext(ignored -> Timber.d("loadFirstPage Intent"));
  }

  @Override public Observable<Boolean> loadNextPageIntent() {
    return RxJavaInterop.toV2Observable(RxRecyclerView.scrollStateChanges(recyclerView))
        .filter(event -> adapter.isLoadingNextPage())
        .filter(event -> event == RecyclerView.SCROLL_STATE_IDLE)
        .filter(
            event -> layoutManager.findLastVisibleItemPosition() == adapter.getItems().size() - 1)
        .map(integer -> true)
        .doOnNext(ignored -> Timber.d("loadNextPage Intent"));
  }

  @Override public Observable<Boolean> pullToRefreshIntent() {
    return RxJavaInterop.toV2Observable(
        RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).map(ignored -> true))
        .doOnNext(ignored -> Timber.d("pullToRefresh Intent"));
  }

  @Override public Observable<String> loadAllProductsFromCategoryIntent() {
    return adapter.loadMoreItemsOfCategoryObservable();
  }

  @Override public void render(HomeViewState viewState) {
    Timber.d("render %s", viewState);
    if (!viewState.isLoadingFirstPage()
        //&& !viewState.isLoadingFirstPage()
        // && !viewState.isLoadingPullToRefresh()
        && viewState.getFirstPageError() == null
      //&& viewState.getNextPageError() == null
      //&& viewState.getPullToRefreshError() == null
        ) {
      renderShowData(viewState);
    } else if (viewState.isLoadingFirstPage()) {
      renderFirstPageLoading();
    } else if (viewState.getFirstPageError() != null) {
      renderFirstPageError();
    }
  }

  private void renderShowData(HomeViewState state) {
    TransitionManager.beginDelayedTransition((ViewGroup) getView());
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
    adapter.setLoadingNextPage(state.isLoadingNextPage());
    adapter.setItems(state.getData());
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
