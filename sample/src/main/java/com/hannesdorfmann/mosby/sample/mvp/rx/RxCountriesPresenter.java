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

package com.hannesdorfmann.mosby.sample.mvp.rx;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class RxCountriesPresenter extends MvpLceRxPresenter<CountriesView, List<Country>>
    implements CountriesPresenter {

  private int failingCounter = 0;

  @Override public void loadCountries(boolean pullToRefresh) {
    subscribe(null, pullToRefresh);
  }

}
