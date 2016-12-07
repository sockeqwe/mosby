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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.sample.mvp.customviewstate.MyCustomActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.activity.CountriesActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.layout.CountriesLayoutActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewpager.ViewPagerActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.NotRetainingCountriesActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.RetainingCountriesActivity;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.RetainingCountriesFragmentEmbededInXmlActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  Demo[] demos;

  @Bind(R.id.listView) ListView listView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    demos = createDemos();
    listView.setAdapter(new ArrayAdapter<Demo>(this, android.R.layout.simple_list_item_1, demos));
    listView.setOnItemClickListener(this);
  }

  private Demo[] createDemos() {
    return new Demo[] {
        new Demo("Simple LceActivity", new Intent(this, CountriesActivity.class)),
        new Demo("Simple LceFragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "CountriesFragment")), new Demo("RetainingViewsState LceFragment",
        new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
            "RetainingCountriesFragment")),
        new Demo("Retaining by using Parcelable ViewsState LceFragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "NotRetainingCountriesFragment")),

        new Demo("Retaining ViewsState LceActivity",
            new Intent(this, RetainingCountriesActivity.class)),
        new Demo("Retaining by using Parcelable ViewsState LceActivity",
            new Intent(this, NotRetainingCountriesActivity.class)),

        new Demo("MVP FrameLayout", new Intent(this, CountriesLayoutActivity.class)),

        new Demo("Custom ViewsState Fragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "CustomViewStateFragment")),

        new Demo("Custom ViewState Activity", new Intent(this, MyCustomActivity.class)),
        new Demo("Nested ViewState CountriesFragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "NestedNotRetainingFragment")),
        new Demo("Nested ViewState CountriesFragment ViewPager",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "NestedNotRetainingViewPagerFragment")),
        new Demo("Retaining ViewState Fragment embededed in activities xml layout ",
            new Intent(this, RetainingCountriesFragmentEmbededInXmlActivity.class)),

        new Demo("ViewPager", new Intent(this, ViewPagerActivity.class)),

        new Demo("ViewPager with FragmentSTATEPagerAdapter",
            new Intent(this, ViewPagerActivity.class).putExtra(ViewPagerActivity.KEY_STATEPAGER,
                true))
    };
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    startActivity(demos[position].intent);
  }

  static class Demo {
    String name;
    Intent intent;

    private Demo(String name, Intent intent) {
      this.name = name;
      this.intent = intent;
    }

    public String toString() {
      return name;
    }
  }
}
