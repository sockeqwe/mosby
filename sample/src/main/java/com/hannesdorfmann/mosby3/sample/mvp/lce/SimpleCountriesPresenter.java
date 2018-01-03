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

package com.hannesdorfmann.mosby3.sample.mvp.lce;

import android.support.annotation.NonNull;
import android.util.Log;
import com.hannesdorfmann.mosby3.mvp.MvpQueuingBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby3.sample.mvp.model.CountriesAsyncLoader;
import com.hannesdorfmann.mosby3.sample.mvp.model.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class SimpleCountriesPresenter extends MvpQueuingBasePresenter<CountriesView>
    implements CountriesPresenter {

  private static int PRESENTER_ID_FOR_LOGGING = 0;
  private final String TAG = "CountriesPresenter" + (PRESENTER_ID_FOR_LOGGING++);

  private int failingCounter = 0;
  private CountriesAsyncLoader countriesLoader;

  public SimpleCountriesPresenter() {
    Log.d(TAG, "constructor " + toString());
  }

  @Override public void loadCountries(final boolean pullToRefresh) {

    Log.d(TAG, "loadCountries(" + pullToRefresh + ")");

    Log.d(TAG, "showLoading(" + pullToRefresh + ")");

    onceViewAttached(new ViewAction<CountriesView>() {
      @Override public void run(@NonNull CountriesView view) {
        view.showLoading(pullToRefresh);
      }
    });

    if (countriesLoader != null && !countriesLoader.isCancelled()) {
      countriesLoader.cancel(true);
    }

    countriesLoader = new CountriesAsyncLoader(++failingCounter % 2 != 0,
        new CountriesAsyncLoader.CountriesLoaderListener() {

          @Override public void onSuccess(final List<Country> countries) {

            Log.d(TAG, "Countries callback onSuccess ");
            onceViewAttached(new ViewAction<CountriesView>() {
              @Override public void run(@NonNull CountriesView view) {
                Log.d(TAG, "setData()");
                view.setData(countries);

                Log.d(TAG, "showContent()");
                view.showContent();
              }
            });
          }

          @Override public void onError(final Exception e) {
            Log.d(TAG, "Countries callback onError ");

            onceViewAttached(new ViewAction<CountriesView>() {
              @Override public void run(@NonNull CountriesView view) {
                Log.d(TAG,
                    "showError(" + e.getClass().getSimpleName() + " , " + pullToRefresh + ")");
                view.showError(e, pullToRefresh);
              }
            });
          }
        });
    countriesLoader.execute();
  }

  @Override public void detachView() {
    super.detachView();
    Log.d(TAG, "View detached from presenter");
  }

  @Override public void destroy() {
    super.destroy();
    if (countriesLoader != null) {
      countriesLoader.cancel(true);
    }
    Log.d(TAG, "Presenter destroyed");
  }

  @Override public void attachView(CountriesView view) {
    super.attachView(view);
    Log.d(TAG, "attach view " + view.toString());
  }
}
