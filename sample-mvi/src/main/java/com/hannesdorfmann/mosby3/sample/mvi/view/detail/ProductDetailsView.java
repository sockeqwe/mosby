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

package com.hannesdorfmann.mosby3.sample.mvi.view.detail;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;

/**
 * @author Hannes Dorfmann
 */

public interface ProductDetailsView extends MvpView {

  /**
   * The intent to load details of a certain product
   */
  public Observable<Integer> loadDetailsIntent();

  /**
   * The intent to add a product to the shopping cart
   */
  public Observable<Product> addToShoppingCartIntent();

  /**
   * the intent to remove a product from the shopping cart
   */
  public Observable<Product> removeFromShoppingCartIntent();

  /**
   * Render the state n the UI
   */
  public void render(ProductDetailsViewState state);
}
