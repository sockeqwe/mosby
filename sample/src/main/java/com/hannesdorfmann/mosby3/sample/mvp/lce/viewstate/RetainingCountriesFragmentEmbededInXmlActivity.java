package com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.hannesdorfmann.mosby3.sample.R;

/**
 * An example showing that embeded fragments (embeded in activities xml layout) are working
 *
 * @author Hannes Dorfmann
 */
public class RetainingCountriesFragmentEmbededInXmlActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_embedded_fragment);
  }
}
