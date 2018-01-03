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

import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;

/**
 * Responsible to show a list of menu items
 *
 * @author Hannes Dorfmann
 */
public interface MainMenuView extends MvpView {

  /**
   * Intent to load all categories
   */
  Observable<Boolean> loadCategoriesIntent();

  /**
   * Intent to select a certain item
   */
  Observable<String> selectCategoryIntent();

  void render(MenuViewState menuViewState);
}
