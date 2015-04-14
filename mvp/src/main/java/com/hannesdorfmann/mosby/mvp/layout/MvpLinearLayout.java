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
package com.hannesdorfmann.mosby.mvp.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A LinearLayout that can be used as view with an presenter
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
public abstract class MvpLinearLayout<P extends MvpPresenter> extends LinearLayout
    implements MvpView {

  protected P presenter;

  public MvpLinearLayout(Context context) {
    super(context);
  }

  public MvpLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MvpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MvpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this, this);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    presenter = createPresenter();
    if (presenter == null) {
      throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
    }
    presenter.attachView(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.detachView(false);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this view
   */
  protected abstract P createPresenter();
}
