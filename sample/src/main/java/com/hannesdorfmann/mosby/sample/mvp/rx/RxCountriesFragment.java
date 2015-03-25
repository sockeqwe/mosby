package com.hannesdorfmann.mosby.sample.mvp.rx;

import com.hannesdorfmann.mosby.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.lce.viewstate.RetainingCountriesFragment;

/**
 * @author Hannes Dorfmann
 */
public class RxCountriesFragment extends RetainingCountriesFragment {

  @Override protected CountriesPresenter createPresenter() {
    return new RxCountriesPresenter();
  }
}
