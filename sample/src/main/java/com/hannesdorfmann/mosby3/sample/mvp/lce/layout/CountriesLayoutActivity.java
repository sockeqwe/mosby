package com.hannesdorfmann.mosby3.sample.mvp.lce.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.hannesdorfmann.mosby3.sample.R;

/**
 * @author Hannes Dorfmann
 */
public class CountriesLayoutActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.countries_mvp_layout);
  }
}
