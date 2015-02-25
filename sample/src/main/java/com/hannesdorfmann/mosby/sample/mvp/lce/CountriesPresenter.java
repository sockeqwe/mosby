package com.hannesdorfmann.mosby.sample.mvp.lce;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesPresenter extends MvpBasePresenter<CountriesView> {

  private int failingCounter = 0;
  private CountriesLoader countriesLoader;

  public void loadCountries(final boolean pullToRefresh) {
    getView().showLoading(pullToRefresh);

    if (countriesLoader != null && !countriesLoader.isCancelled()){
      countriesLoader.cancel(true);
    }

    countriesLoader = new CountriesLoader(++failingCounter % 2 != 0, new CountriesLoader.CountriesLoaderListener() {

      @Override public void onSuccess(List<Country> countries) {

        if (isViewAttached()) {
          getView().setData(countries);
          getView().showContent();
        }
      }

      @Override public void onError(Exception e) {

        if (isViewAttached()) {
          getView().showError(e, pullToRefresh);
        }
      }
    });
    countriesLoader.execute();
  }

  @Override public void onDestroy(boolean retainInstance) {
    super.onDestroy(retainInstance);

    if (countriesLoader != null) {
      countriesLoader.cancel(true);
    }
  }
}
