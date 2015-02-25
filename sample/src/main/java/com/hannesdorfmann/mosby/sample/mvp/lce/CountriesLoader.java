package com.hannesdorfmann.mosby.sample.mvp.lce;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesLoader extends AsyncTask<Void, Void, List<Country>> {

  public interface CountriesLoaderListener {
    public void onSuccess(List<Country> countries);

    public void onError(Exception e);
  }

  private boolean shouldFail;
  private CountriesLoaderListener listener;

  public CountriesLoader(boolean shouldFail, CountriesLoaderListener listener) {
    this.listener = listener;
    this.shouldFail = shouldFail;
  }

  @Override protected List<Country> doInBackground(Void... params) {

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return generateCountries();
  }

  private List<Country> generateCountries() {
    ArrayList<Country> countries = new ArrayList<Country>(20);


    countries.add(new Country("Italy"));
    countries.add(new Country("Germany"));
    countries.add(new Country("Belgium"));
    countries.add(new Country("Austria"));
    countries.add(new Country("Brazil"));
    countries.add(new Country("Chile"));
    countries.add(new Country("China"));
    countries.add(new Country("Denmark"));
    countries.add(new Country("Finnland"));
    countries.add(new Country("France"));
    countries.add(new Country("Ghana"));
    countries.add(new Country("Japan"));
    countries.add(new Country("Mexico"));
    countries.add(new Country("Netherlands"));
    countries.add(new Country("Norway"));
    countries.add(new Country("Spain"));
    countries.add(new Country("Switzerland"));
    countries.add(new Country("United Kingdom"));
    countries.add(new Country("United States"));
    countries.add(new Country("Australia"));

    Collections.shuffle(countries);

    return countries;
  }

  @Override protected void onPostExecute(List<Country> countries) {

    if (isCancelled()){
      return;
    }

    if (shouldFail) {
      listener.onError(new Exception("Oops something went wrong"));
    } else {
      listener.onSuccess(countries);
    }
  }
}
