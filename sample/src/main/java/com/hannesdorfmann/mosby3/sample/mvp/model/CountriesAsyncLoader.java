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

package com.hannesdorfmann.mosby3.sample.mvp.model;

import android.os.AsyncTask;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesAsyncLoader extends AsyncTask<Void, Void, List<Country>> {

  public interface CountriesLoaderListener {
    public void onSuccess(List<Country> countries);

    public void onError(Exception e);
  }

  private boolean shouldFail;
  private CountriesLoaderListener listener;

  public CountriesAsyncLoader(boolean shouldFail, CountriesLoaderListener listener) {
    this.listener = listener;
    this.shouldFail = shouldFail;
  }

  @Override protected List<Country> doInBackground(Void... params) {

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      return null;
    }

    List<Country> countries = CountryApi.getCountries();
    Collections.shuffle(countries);

    return countries;
  }

  @Override protected void onPostExecute(List<Country> countries) {

    if (isCancelled() || countries == null) {
      return;
    }

    if (shouldFail) {
      listener.onError(new Exception("Oops something went wrong"));
    } else {
      listener.onSuccess(countries);
    }
  }
}
