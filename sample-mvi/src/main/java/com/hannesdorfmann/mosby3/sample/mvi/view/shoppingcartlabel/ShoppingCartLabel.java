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

package com.hannesdorfmann.mosby3.sample.mvi.view.shoppingcartlabel;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegate;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateCallback;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateImpl;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * A UI widget that displays how many items are in the shopping cart
 * @author Hannes Dorfmann
 */
public class ShoppingCartLabel extends AppCompatButton implements ShoppingCartLabelView,
    ViewGroupMviDelegateCallback<ShoppingCartLabelView, ShoppingCartLabelPresenter> {

  private final ViewGroupMviDelegate<ShoppingCartLabelView, ShoppingCartLabelPresenter>
      mviDelegate = new ViewGroupMviDelegateImpl<>(this);

  public ShoppingCartLabel(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @NonNull @Override public ShoppingCartLabelView getMvpView() {
    return this;
  }

  @NonNull @Override public ShoppingCartLabelPresenter createPresenter() {
    Timber.d("create presenter");
    return SampleApplication.getDependencyInjection(getContext()).newShoppingCartLabelPresenter();
  }

  @Override public Parcelable superOnSaveInstanceState() {
    return super.onSaveInstanceState();
  }

  @Override public void superOnRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);
  }

  @Override public Observable<Boolean> loadIntent() {
    return Observable.just(true);
  }

  @Override public void render(int itemsInShoppingCart) {
    // TODO move to strings.xml / internationalization with plurals
    Timber.d("render %d items in shopping cart", itemsInShoppingCart);
    if (itemsInShoppingCart == 0) {
      setText("0 items");
    } else if (itemsInShoppingCart == 1) {
      setText("1 item");
    } else {
      setText(itemsInShoppingCart + " items");
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

  @Override public void setRestoringViewState(boolean restoringViewState) {
    // Not needed for this view
  }
}
