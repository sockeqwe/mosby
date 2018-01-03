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

package com.hannesdorfmann.mosby3.sample.mvp;

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby3.sample.mvp.model.Country;
import java.util.List;

/**
 * The View interface. It's not really needed to do it this way (defining a own interface).
 * We could also use Mvp MvpLceView<List<Country>>
 * directly instead.
 *
 * @author Hannes Dorfmann
 */
public interface CountriesView extends MvpLceView<List<Country>> {
}
