package com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate;

import android.os.Bundle;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState;
import com.hannesdorfmann.mosby3.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby3.sample.mvp.model.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class RetainingCountriesActivity extends NotRetainingCountriesActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override public LceViewState<List<Country>, CountriesView> createViewState() {
    setRetainInstance(true);
    return new RetainingLceViewState<>();
  }
}
