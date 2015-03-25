package com.hannesdorfmann.mosby.sample.mvp;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * @author Hannes Dorfmann
 */
public interface CountriesPresenter extends MvpPresenter<CountriesView>{

  public void loadCountries(final boolean pullToRefresh);
}
