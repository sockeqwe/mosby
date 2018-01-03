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

import android.content.Context;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.mvi.layout.MviFrameLayout;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */
public class MainMenuLayout extends MviFrameLayout<MainMenuView, MainMenuPresenter>
    implements MainMenuView {

  private final MainMenuAdapter adapter;
  @BindView(R.id.loadingView) View loadingView;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.errorView) View errorView;

  public MainMenuLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    inflate(context, R.layout.view_mainmenu, this);
    ButterKnife.bind(this, this);

    adapter = new MainMenuAdapter(LayoutInflater.from(context));
    recyclerView.setAdapter(adapter);
  }

  @Override public MainMenuPresenter createPresenter() {
    Timber.d("Create MainMenuPresenter");
    return SampleApplication.getDependencyInjection(getContext()).getMainMenuPresenter();
  }

  @Override public Observable<Boolean> loadCategoriesIntent() {
    return Observable.just(true);
  }

  @Override public Observable<String> selectCategoryIntent() {
    return adapter.getSelectedItemObservable();
  }

  @Override public void render(MenuViewState menuViewState) {
    Timber.d("Render %s", menuViewState);

    TransitionManager.beginDelayedTransition(this);
    if (menuViewState instanceof MenuViewState.LoadingState) {
      loadingView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      errorView.setVisibility(View.GONE);
    } else if (menuViewState instanceof MenuViewState.DataState) {
      adapter.setItems(((MenuViewState.DataState) menuViewState).getCategories());
      adapter.notifyDataSetChanged();
      loadingView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
      errorView.setVisibility(View.GONE);
    } else if (menuViewState instanceof MenuViewState.ErrorState) {
      loadingView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.GONE);
      errorView.setVisibility(View.VISIBLE);
    } else {
      throw new IllegalStateException("Unknown state " + menuViewState);
    }
  }
}
