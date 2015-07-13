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

import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mvp.customviewstate.MyCustomActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.activity.CountriesActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.layout.CountriesLayoutActivity;
import com.hannesdorfmann.mosby.sample.mvp.lce.viewstate.CountriesViewStateActivity;

import butterknife.Bind;

public class MainActivity extends MosbyActivity implements AdapterView.OnItemClickListener {

  Demo[] demos;

  @Bind(R.id.listView) ListView listView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
        new Demo("ViewsState LceActivity", new Intent(this, CountriesViewStateActivity.class)),

        new Demo("MVP FrameLayout", new Intent(this, CountriesLayoutActivity.class)),

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
