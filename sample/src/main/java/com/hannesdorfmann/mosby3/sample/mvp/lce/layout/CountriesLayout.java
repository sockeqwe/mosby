package com.hannesdorfmann.mosby3.sample.mvp.lce.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.layout.MvpViewStateFrameLayout;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.hannesdorfmann.mosby3.sample.R;
import com.hannesdorfmann.mosby3.sample.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesAdapter;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesErrorMessage;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby3.sample.mvp.lce.SimpleCountriesPresenter;
import com.hannesdorfmann.mosby3.sample.mvp.model.Country;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Hannes Dorfmann
 */
public class CountriesLayout extends MvpViewStateFrameLayout<CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.loadingView) View loadingView;
  @Bind(R.id.errorView) TextView errorView;
  @Bind(R.id.contentView) SwipeRefreshLayout contentView;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;

  private CountriesAdapter adapter;

  public CountriesLayout(Context context) {
    super(context);
  }

  public CountriesLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CountriesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public CountriesLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this, this);

    contentView.setOnRefreshListener(this);

    adapter = new CountriesAdapter(getContext());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  @Override public CountriesPresenter createPresenter() {
    return new SimpleCountriesPresenter();
  }

  @Override public ViewState<CountriesView> createViewState() {
    return new CastedArrayListLceViewState<List<Country>, CountriesView>();
  }

  @Override public CastedArrayListLceViewState<List<Country>, CountriesView> getViewState() {
    return (CastedArrayListLceViewState) super.getViewState();
  }

  @Override public void onNewViewStateInstance() {
    loadData(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    errorView.setVisibility(View.GONE);

    if (pullToRefresh) {
      if (pullToRefresh && !contentView.isRefreshing()) {
        // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
        contentView.post(new Runnable() {
          @Override public void run() {
            contentView.setRefreshing(true);
          }
        });
      }
      contentView.setVisibility(View.VISIBLE);
    } else {
      loadingView.setVisibility(View.VISIBLE);
      contentView.setVisibility(View.GONE);
    }
    getViewState().setStateShowLoading(pullToRefresh);
  }

  @Override public void showContent() {
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    contentView.setVisibility(View.VISIBLE);

    contentView.setRefreshing(false);
    getViewState().setStateShowContent(adapter.getCountries());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    getViewState().setStateShowError(e, pullToRefresh);

    String msg = CountriesErrorMessage.get(e, pullToRefresh, getContext());

    loadingView.setVisibility(View.GONE);
    if (pullToRefresh) {
      contentView.setRefreshing(false);
      if (!isRestoringViewState()) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
      }
    } else {
      contentView.setVisibility(View.GONE);
      errorView.setText(msg);
      errorView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @OnClick(R.id.errorView) public void onErrorViewClicked() {
    loadData(false);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    SampleApplication.getRefWatcher(getContext()).watch(this);
  }
}
