/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.hannesdorfmann.mosby3.sample.mvp.customviewstate.MyCustomFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.fragment.CountriesFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.NotRetainingCountriesFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.RetainingCountriesFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.nested.NestedFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.nested.NestedViewPagerFragment;

/**
 * @author Hannes Dorfmann
 */
public class FragmentContainerActivity extends AppCompatActivity {

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

    if ("CustomViewStateFragment".equals(fragmentName)){
      return new MyCustomFragment();
    }

    if ("NestedNotRetainingFragment".equals(fragmentName)){
      return new NestedFragment();
    }

    if ("NestedNotRetainingViewPagerFragment".equals(fragmentName)){
      return new NestedViewPagerFragment();
    }

    getSupportActionBar().setTitle(fragmentName);

    return null;
  }
}
