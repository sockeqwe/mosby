/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.utils.fragment.integrationtest.backstack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.hannesdorfmann.mosby3.utils.fragment.integrationtest.R;

public class BackstackActivityWithChildFragments extends AppCompatActivity {

  private static final String TAG_BACKSTACK = "fragmentBackstack";
  private static final String TAG_NOT_BACKSTACK = "fragmentNotBackstack";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backstack);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.backstackFragmentsContainer, new FragmentOnBackstack(), TAG_BACKSTACK)
          .addToBackStack(null)
          .commit();

      getSupportFragmentManager().beginTransaction()
          .replace(R.id.simpleFragmentsContainer, new SimpleFragmentNotOnBackstack(),
              TAG_NOT_BACKSTACK)
          .commit();
    }
  }

  public FragmentOnBackstack getFragmentOnBackstack() {
    return (FragmentOnBackstack) getSupportFragmentManager().findFragmentByTag(TAG_BACKSTACK);
  }

  public SimpleFragmentNotOnBackstack getFragmentNotOnBackstack() {
    return (SimpleFragmentNotOnBackstack) getSupportFragmentManager().findFragmentByTag(TAG_NOT_BACKSTACK);
  }
}
