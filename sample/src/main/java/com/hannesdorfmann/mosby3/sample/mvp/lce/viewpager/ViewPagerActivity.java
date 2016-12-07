/*
 * Copyright (c) 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample.mvp.lce.viewpager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.hannesdorfmann.mosby3.sample.R;
import com.hannesdorfmann.mosby3.sample.mvp.customviewstate.MyCustomFragment;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.RetainingCountriesFragment;

/**
 * @author Hannes Dorfmann
 */
public class ViewPagerActivity extends AppCompatActivity {

  public static final String KEY_STATEPAGER = "ViewPagerActivity.STATE_PATER";

  ViewPager viewPager;
  TabLayout tabLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewpager);
    viewPager = (ViewPager) findViewById(R.id.viewPager);
    tabLayout = (TabLayout) findViewById(R.id.tabLayout);

    if (getIntent().getBooleanExtra(KEY_STATEPAGER, false)) {
      viewPager.setAdapter(new MyFragmentStatePagerAdapter(getSupportFragmentManager()));
    } else {
      viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
    }

    tabLayout.setupWithViewPager(viewPager);
  }

  class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      return position % 2 == 0 ? new RetainingCountriesFragment() : new MyCustomFragment();
    }

    @Override public int getCount() {
      return 10;
    }

    @Override public CharSequence getPageTitle(int position) {
      return "Tab " + position;
    }
  }

  class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    public MyFragmentStatePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      return position % 2 == 0 ? new RetainingCountriesFragment() : new MyCustomFragment();
    }

    @Override public int getCount() {
      return 10;
    }

    @Override public CharSequence getPageTitle(int position) {
      return "Tab " + position;
    }
  }
}
