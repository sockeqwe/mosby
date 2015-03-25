package com.hannesdorfmann.mosby.sample.mvp.model;

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
      e.printStackTrace();
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
