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

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.ProductDetail;

/**
 * The View state for the Menu
 *
 * @author Hannes Dorfmann
 */
public interface ProductDetailsViewState {

  /**
   * Loads the list of all menu items
   */
  public final class LoadingState implements ProductDetailsViewState {

    @Override public String toString() {
      return "LoadingState{}";
    }
  }

  /**
   * Ane error has ocurred while loading the data
   */
  public final class ErrorState implements ProductDetailsViewState {
    private final Throwable error;

    public ErrorState(Throwable error) {
      this.error = error;
    }

    public Throwable getError() {
      return error;
    }

    @Override public String toString() {
      return "ErrorState{" +
          "error=" + error +
          '}';
    }
  }

  /**
   * Data has been loaded successfully and can now be displayed
   */
  public final class DataState implements ProductDetailsViewState {
    private final ProductDetail detail;

    public DataState(ProductDetail detail) {
      this.detail = detail;
    }

    public ProductDetail getDetail() {
      return detail;
    }

    @Override public String toString() {
      return "DataState{" +
          "detail=" + detail +
          '}';
    }
  }
}
