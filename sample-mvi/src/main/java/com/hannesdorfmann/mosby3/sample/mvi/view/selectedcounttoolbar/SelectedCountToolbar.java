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

package com.hannesdorfmann.mosby3.sample.mvi.view.selectedcounttoolbar;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegate;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateCallback;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateImpl;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class SelectedCountToolbar extends Toolbar implements SelectedCountToolbarView,
    ViewGroupMviDelegateCallback<SelectedCountToolbarView, SelectedCountToolbarPresenter> {

  private final ViewGroupMviDelegate<SelectedCountToolbarView, SelectedCountToolbarPresenter>
      mviDelegate = new ViewGroupMviDelegateImpl<>(this);

  private final PublishSubject<Boolean> clearSelectionIntent = PublishSubject.create();
  private final PublishSubject<Boolean> deleteSelectedItemsIntent = PublishSubject.create();

  public SelectedCountToolbar(Context context, AttributeSet attrs) {
    super(context, attrs);
    setNavigationOnClickListener(v -> clearSelectionIntent.onNext(true));
    setNavigationIcon(R.drawable.ic_back_selection_count_toolbar);
    inflateMenu(R.menu.shopping_cart_toolbar);
    setOnMenuItemClickListener(item -> {
      deleteSelectedItemsIntent.onNext(true);
      return true;
    });
  }

  @Override public Observable<Boolean> clearSelectionIntent() {
    return clearSelectionIntent;
  }

  @Override public Observable<Boolean> deleteSelectedItemsIntent() {
    return deleteSelectedItemsIntent;
  }

  @NonNull @Override public SelectedCountToolbarView getMvpView() {
    return this;
  }

  @NonNull @Override public SelectedCountToolbarPresenter createPresenter() {
    Timber.d("create presenter");
    return SampleApplication.getDependencyInjection(getContext())
        .newSelectedCountToolbarPresenter();
  }

  @Override public void render(int selectedCount) {
    Timber.d("render %d selected items", selectedCount);
    if (selectedCount == 0) {
      if (getVisibility() == View.VISIBLE) {
        animate().alpha(0f).withEndAction(() -> setVisibility(View.GONE)).start();
      } else {
        setVisibility(View.GONE);
      }
    } else {
      // TODO remove hardcoded strings - move to stings.xml with plurals
      if (selectedCount == 1) {
        setTitle(selectedCount + " Item");
      } else {
        setTitle(selectedCount + " Items");
      }

      if (getVisibility() != View.VISIBLE) {
        animate().alpha(1f).withStartAction(() -> setVisibility(View.VISIBLE)).start();
      } else {
        setVisibility(View.VISIBLE);
      }
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mviDelegate.onAttachedToWindow();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mviDelegate.onDetachedFromWindow();
  }

  @Override public Parcelable onSaveInstanceState() {
    return mviDelegate.onSaveInstanceState();
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    mviDelegate.onRestoreInstanceState(state);
  }

  @Override public Parcelable superOnSaveInstanceState() {
    return super.onSaveInstanceState();
  }

  @Override public void superOnRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    // Don't needed for this view
  }

}
