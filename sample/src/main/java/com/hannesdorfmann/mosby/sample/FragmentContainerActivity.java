package com.hannesdorfmann.mosby.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.fragment.CountriesFragment;
import com.hannesdorfmann.mosby.sample.mvp.lce.viewstate.NotRetainingCountriesFragment;
import com.hannesdorfmann.mosby.sample.mvp.lce.viewstate.RetainingCountriesFragment;

/**
 * @author Hannes Dorfmann
 */
public class FragmentContainerActivity extends MosbyActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment_container);

    if (savedInstanceState == null) {
      Fragment f = getFragment();
      if (f == null) {
        Toast.makeText(this, "Error: No fragment specified", Toast.LENGTH_SHORT).show();
        finish();
      } else {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).commit();
      }
    }
  }

  private Fragment getFragment() {
    String fragmentName = getIntent().getStringExtra("fragment");
    if (fragmentName == null) {
      return null;
    }

    if ("CountriesFragment".equals(fragmentName)) {
      return new CountriesFragment();
    }

    if ("RetainingCountriesFragment".equals(fragmentName)){
      return new RetainingCountriesFragment();
    }

    if ("NotRetainingCountriesFragment".equals(fragmentName)){
      return new NotRetainingCountriesFragment();
    }

    return null;
  }
}
