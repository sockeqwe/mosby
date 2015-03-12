package com.hannesdorfmann.mosby.sample.mvp.lce;

import android.util.Log;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesPresenter extends MvpBasePresenter<CountriesView> {

  private static final String TAG = "CountriesPresenter";

  private int failingCounter = 0;
  private CountriesLoader countriesLoader;

  public void loadCountries(final boolean pullToRefresh) {

    Log.d(TAG, "loadCountries(" + pullToRefresh + ")");

    Log.d(TAG, "showLoading(" + pullToRefresh + ")");

    getView().showLoading(pullToRefresh);

    if (countriesLoader != null && !countriesLoader.isCancelled()) {
      countriesLoader.cancel(true);
    }

    countriesLoader = new CountriesLoader(++failingCounter % 2 != 0,
        new CountriesLoader.CountriesLoaderListener() {

          @Override public void onSuccess(List<Country> countries) {

            if (isViewAttached()) {
              Log.d(TAG, "setData()");
              getView().setData(countries);

              Log.d(TAG, "showContent()");
              getView().showContent();
            }
          }

          @Override public void onError(Exception e) {

            if (isViewAttached()) {

              Log.d(TAG, "showError("+e.getClass().getSimpleName()+" , " + pullToRefresh + ")");
              getView().showError(e, pullToRefresh);
            }
          }
        });
    countriesLoader.execute();
  }

  @Override public void onDestroy(boolean retainInstance) {
    super.onDestroy(retainInstance);

    if (!retainInstance && countriesLoader != null) {
      countriesLoader.cancel(true);
      Log.d(TAG, "onDestroy() --> cancel Loader");
    }
  }
}
