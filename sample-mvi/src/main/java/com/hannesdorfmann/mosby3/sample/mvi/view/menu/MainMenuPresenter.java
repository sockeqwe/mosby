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

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.http.ProductBackendApiDecorator;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.MainMenuItem;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */
public class MainMenuPresenter extends MviBasePresenter<MainMenuView, MenuViewState> {
  private final ProductBackendApiDecorator backendApi;

  public MainMenuPresenter(ProductBackendApiDecorator backendApi) {
    this.backendApi = backendApi;
  }

  @Override protected void bindIntents() {

    Observable<List<String>> loadCategories = intent(MainMenuView::loadCategoriesIntent)
        .doOnNext(categoryName -> Timber.d("intent: load category %s", categoryName))
        .flatMap(ignored -> backendApi.getAllCategories().subscribeOn(Schedulers.io()));

    Observable<String> selectCategory =
        intent(MainMenuView::selectCategoryIntent)
            .doOnNext(categoryName -> Timber.d("intent: select category %s", categoryName))
            .startWith(MainMenuItem.HOME);

    List<Observable<?>> allIntents = new ArrayList<>(2);
    allIntents.add(loadCategories);
    allIntents.add(selectCategory);

    Observable<MenuViewState> menuViewStateObservable =
        Observable.combineLatest(allIntents, (Function<Object[], MenuViewState>) objects -> {
          List<String> categories = (List<String>) objects[0];
          String selectedCategory = (String) objects[1];

          List<MainMenuItem> categoriesItems = new ArrayList<MainMenuItem>(categories.size() + 1);
          categoriesItems.add(
              new MainMenuItem(MainMenuItem.HOME, selectedCategory.equals(MainMenuItem.HOME)));

          for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            categoriesItems.add(new MainMenuItem(category, category.equals(selectedCategory)));
          }

          return new MenuViewState.DataState(categoriesItems);
        })
            .startWith(new MenuViewState.LoadingState())
            .onErrorReturn(MenuViewState.ErrorState::new)
            .observeOn(AndroidSchedulers.mainThread());

    subscribeViewState(menuViewStateObservable, MainMenuView::render);
  }
}
