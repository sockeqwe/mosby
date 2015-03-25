package com.hannesdorfmann.mosby.sample.mvp;

import android.content.Context;
import com.hannesdorfmann.mosby.sample.R;

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
