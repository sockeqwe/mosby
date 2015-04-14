package com.hannesdorfmann.mosby.sample.mvp.lce.layout;

import android.os.Bundle;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.R;

/**
 * @author Hannes Dorfmann
 */
public class CountriesLayoutActivity extends MosbyActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.countries_mvp_layout);
  }
}
