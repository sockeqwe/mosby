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

package com.hannesdorfmann.mosby.mvp;

import android.os.Bundle;
import com.hannesdorfmann.mosby.MosbyActivity;

/**
 * A {@link MosbyActivity} that uses an {@link MvpPresenter} to implement a Model-View-Presenter
 * Architecture.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpActivity<P extends MvpPresenter> extends MosbyActivity implements MvpView {

  protected P presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = createPresenter();
    presenter.attachView(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView(false);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this view
   */
  protected abstract P createPresenter();
}
