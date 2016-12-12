package com.hannesdorfmann.mosby3.sample.mvi.view.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.TransitionManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.view.ui.GridSpacingItemDecoration;
import com.jakewharton.rxbinding.widget.RxSearchView;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class SearchActivity extends MviActivity<SearchView, SearchPresenter> implements SearchView {

  @BindView(R.id.searchView) android.widget.SearchView searchView;
  @BindView(R.id.container) ViewGroup container;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.errorView) TextView errorView;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.emptyView) View emptyView;
  @BindInt(R.integer.grid_span_size) int spanSize;

  private SearchAdapter adapter;
  private Unbinder unbinder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    unbinder = ButterKnife.bind(this);

    adapter = new SearchAdapter(getLayoutInflater());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new GridLayoutManager(this, spanSize));
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanSize,
        getResources().getDimensionPixelSize(R.dimen.grid_spacing), true));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  @NonNull @Override public SearchPresenter createPresenter() {
    Timber.d("createPresenter");
    return SampleApplication.getDependencyInjection(this).newSearchPresenter();
  }

  @Override public Observable<String> searchIntent() {
    return RxJavaInterop.toV2Observable(RxSearchView.queryTextChanges(searchView))
        .skip(2) // Because after screen orientation changes query Text will be resubmitted again
        .filter(queryString -> queryString.length() > 3 || queryString.length() == 0)
        .debounce(500, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .map(CharSequence::toString)
        .doOnNext(s -> Timber.d("Search Intent '%s'", s));
  }

  @Override public void render(SearchViewState viewState) {
    Timber.d("render %s", viewState);
    if (viewState instanceof SearchViewState.SearchNotStartedYet) {
      renderSearchNotStarted();
    } else if (viewState instanceof SearchViewState.Loading) {
      renderLoading();
    } else if (viewState instanceof SearchViewState.SearchResult) {
      renderResult(((SearchViewState.SearchResult) viewState).getResult());
    } else if (viewState instanceof SearchViewState.EmptyResult) {
      renderEmptyResult();
    } else if (viewState instanceof SearchViewState.Error) {
      renderError();
    } else {
      throw new IllegalArgumentException("Don't know how to render viewState " + viewState);
    }
  }

  private void renderResult(List<Product> result) {
    TransitionManager.beginDelayedTransition(container);
    recyclerView.setVisibility(View.VISIBLE);
    loadingView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    adapter.setProducts(result);
    adapter.notifyDataSetChanged();
  }

  private void renderSearchNotStarted() {
    TransitionManager.beginDelayedTransition(container);
    recyclerView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
  }

  private void renderLoading() {
    TransitionManager.beginDelayedTransition(container);
    recyclerView.setVisibility(View.GONE);
    loadingView.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
  }

  private void renderError() {
    TransitionManager.beginDelayedTransition(container);
    recyclerView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.VISIBLE);
    emptyView.setVisibility(View.GONE);
  }

  private void renderEmptyResult() {
    TransitionManager.beginDelayedTransition(container);
    recyclerView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
  }
}
