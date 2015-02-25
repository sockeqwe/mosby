package com.hannesdorfmann.mosby.sample.mvp.lce.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;
import com.hannesdorfmann.mosby.sample.R;
import com.hannesdorfmann.mosby.sample.mvp.lce.CountriesAdapter;
import com.hannesdorfmann.mosby.sample.mvp.lce.CountriesErrorMessage;
import com.hannesdorfmann.mosby.sample.mvp.lce.CountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.lce.CountriesView;
import com.hannesdorfmann.mosby.sample.mvp.lce.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesFragment
    extends MvpLceFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  CountriesAdapter adapter;

  @Override public void init(@Nullable View view, @Nullable Bundle savedInstance) {
    // Setup contentView == SwipeRefreshView
    contentView.setOnRefreshListener(this);

    // Setup recycler view
    adapter = new CountriesAdapter(getActivity());
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);
    loadData(false);
  }

  private void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  @Override protected String getErrorMessage(Exception e, boolean pullToRefresh) {
    return CountriesErrorMessage.get(e, pullToRefresh, getActivity());
  }

  @Override protected void onErrorViewClicked() {
    loadData(false);
  }

  @Override protected CountriesPresenter createPresenter() {
    return new CountriesPresenter();
  }

  @Override protected Integer getLayoutRes() {
    return R.layout.countries_list;
  }

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showError(Exception e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }
}
