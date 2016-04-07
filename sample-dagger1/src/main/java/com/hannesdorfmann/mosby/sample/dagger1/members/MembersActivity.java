/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.dagger1.members;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.ButterKnife;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.ParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.hannesdorfmann.mosby.sample.dagger1.Injector;
import com.hannesdorfmann.mosby.sample.dagger1.R;
import com.hannesdorfmann.mosby.sample.dagger1.model.ErrorMessageDeterminer;
import com.hannesdorfmann.mosby.sample.dagger1.model.User;

import dagger.ObjectGraph;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Hannes Dorfmann
 */
public class MembersActivity extends MvpLceViewStateActivity<SwipeRefreshLayout, List<User>, MembersView, MembersPresenter>
    implements MembersView, SwipeRefreshLayout.OnRefreshListener, Injector {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  @Inject ErrorMessageDeterminer errorMessageDeterminer;
  MembersAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_members);
    ButterKnife.bind(this);
    injectDependencies();

    adapter = getObjectGraph().get(MembersAdapter.class);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    contentView.setOnRefreshListener(this);
  }

  @Override public ParcelableLceViewState<List<User>, MembersView> createViewState() {
    return new CastedArrayListLceViewState<>();
  }

  protected void injectDependencies() {
    getObjectGraph().inject(this);
  }

  @Override public List<User> getData() {
    return adapter.getMembers();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return errorMessageDeterminer.getErrorMessage(e, pullToRefresh);
  }

  @NonNull @Override public MembersPresenter createPresenter() {
    return getObjectGraph().get(MembersPresenter.class);
  }

  @Override public void setData(List<User> data) {
    adapter.setMembers(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadSquareMembers(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
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

  @Override public ObjectGraph getObjectGraph() {
    return ((Injector) getApplication()).getObjectGraph();
  }
}
