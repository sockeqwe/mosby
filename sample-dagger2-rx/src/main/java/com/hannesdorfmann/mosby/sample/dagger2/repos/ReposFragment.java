package com.hannesdorfmann.mosby.sample.dagger2.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.hannesdorfmann.mosby.sample.dagger2.R;
import com.hannesdorfmann.mosby.sample.dagger2.SampleModule;
import com.hannesdorfmann.mosby.sample.dagger2.model.ErrorMessageDeterminer;
import com.hannesdorfmann.mosby.sample.dagger2.model.Repo;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Hannes Dorfmann
 */
public class ReposFragment
    extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Repo>, ReposView, ReposPresenter>
    implements ReposView, SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  @Inject ErrorMessageDeterminer errorMessageDeterminer;
  ReposComponent reposComponent;
  ReposAdapter adapter;

  protected void injectDependencies() {
    reposComponent =
        DaggerReposComponent.builder().sampleModule(new SampleModule(getActivity())).build();
    reposComponent.inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    injectDependencies();
    return inflater.inflate(R.layout.fragment_repos, container, false);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    adapter = reposComponent.adapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    contentView.setOnRefreshListener(this);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  @Override public LceViewState<List<Repo>, ReposView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public List<Repo> getData() {
    return adapter.getRepos();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return errorMessageDeterminer.getErrorMessage(e, pullToRefresh);
  }

  @Override public ReposPresenter createPresenter() {
    return reposComponent.presenter();
  }

  @Override public void setData(List<Repo> data) {
    adapter.setRepos(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadRepos(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
    e.printStackTrace();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    if (pullToRefresh && !contentView.isRefreshing()) {
      // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
      contentView.post(new Runnable() {
        @Override public void run() {
          contentView.setRefreshing(true);
        }
      });
    }
  }
}
