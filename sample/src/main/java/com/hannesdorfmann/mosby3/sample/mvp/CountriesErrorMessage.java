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

import android.content.Context;
import com.hannesdorfmann.mosby3.sample.R;

/**
 * @author Hannes Dorfmann
 */
public class CountriesErrorMessage {

  public static String get(Throwable e, boolean pullToRefresh, Context c) {
    // TODO distinguish type of exception and retrun different strings
    if (pullToRefresh) {
      return c.getString(R.string.error_countries);
    } else {
      return c.getString(R.string.error_countries_retry);
    }
  }
}
