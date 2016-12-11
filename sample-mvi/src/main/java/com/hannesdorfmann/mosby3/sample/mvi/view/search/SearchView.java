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

package com.hannesdorfmann.mosby3.sample.mvi.view.search;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;

/**
 * @author Hannes Dorfmann
 */
public interface SearchView extends MvpView {

  /**
   * The search intent
   *
   * @return An observable emitting search query text
   */
  Observable<String> searchIntent();

  /**
   * Renders the viewState
   *
   * @param viewState The current viewState state that should be displayed
   */
  void render(SearchViewState viewState);
}
