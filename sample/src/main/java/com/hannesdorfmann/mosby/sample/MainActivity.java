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

package com.hannesdorfmann.mosby.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mvp.customviewstate.MyCustomActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.activity.CountriesActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.viewstate.CountriesViewStateActivity;

public class MainActivity extends MosbyActivity implements AdapterView.OnItemClickListener {

  Demo[] demos;

  @InjectView(R.id.listView) ListView listView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    demos = createDemos();
    listView.setAdapter(new ArrayAdapter<Demo>(this, android.R.layout.simple_list_item_1, demos));
    listView.setOnItemClickListener(this);
  }

  private Demo[] createDemos() {
    return new Demo[] {
        new Demo("MvpLcsActivity", new Intent(this, CountriesActivity.class)),
        new Demo("MvpLcsFragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "CountriesFragment")), new Demo("RetainingViewsStateFragment",
        new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
            "RetainingCountriesFragment")),
        new Demo("Retaining by using Parcelable ViewsStateFragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "NotRetainingCountriesFragment")),
        new Demo("LCE Activity + ViewsState", new Intent(this, CountriesViewStateActivity.class)),
        new Demo("Custom ViewsState Fragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "CustomViewStateFragment")),

        new Demo("Custom ViewsState Fragment",
            new Intent(this, FragmentContainerActivity.class).putExtra("fragment",
                "CustomViewStateFragment")),

        new Demo("Custom ViewState Activity", new Intent(this, MyCustomActivity.class))
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
