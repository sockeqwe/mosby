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

package com.hannesdorfmann.mosby3.sample.mvi.view.category;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;

/**
 * View that shows all items of a given category
 *
 * @author Hannes Dorfmann
 */
public interface CategoryView extends MvpView {

  /**
   * Observable that triggers the intent to load the data
   *
   * @return Observable containing the category name as String
   */
  public Observable<String> loadIntents();

  /**
   * Render the state on the view
   */
  public void render(CategoryViewState state);
}
