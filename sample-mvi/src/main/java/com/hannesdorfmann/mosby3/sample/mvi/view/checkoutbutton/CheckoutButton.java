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

package com.hannesdorfmann.mosby3.sample.mvi.view.checkoutbutton;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegate;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateCallback;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateImpl;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import io.reactivex.Observable;
import java.util.Locale;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */

public class CheckoutButton extends Button implements CheckoutButtonView,
    ViewGroupMviDelegateCallback<CheckoutButtonView, CheckoutButtonPresenter> {

  private final ViewGroupMviDelegate<CheckoutButtonView, CheckoutButtonPresenter> mviDelegate =
      new ViewGroupMviDelegateImpl<>(this);

  public CheckoutButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOnClickListener(v -> {
      Toast.makeText(context, "This is just a demo app. You can't purchase any items.",
          Toast.LENGTH_LONG).show();
    });
  }

  @NonNull @Override public CheckoutButtonView getMvpView() {
    return this;
  }

  @NonNull @Override public CheckoutButtonPresenter createPresenter() {
    Timber.d("create presenter");
    return SampleApplication.getDependencyInjection(getContext()).newCheckoutButtonPresenter();
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

  @Override public void render(double priceSum) {
    // TODO move to strings.xml / internationalization

    String priceString = String.format(Locale.US, "%.2f", priceSum);
    Timber.d("render %s ", priceString);
    if (priceSum == 0) {
      setVisibility(View.INVISIBLE);
    } else {
      setText("Checkout $ " + priceString);
      setVisibility(View.VISIBLE);
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
