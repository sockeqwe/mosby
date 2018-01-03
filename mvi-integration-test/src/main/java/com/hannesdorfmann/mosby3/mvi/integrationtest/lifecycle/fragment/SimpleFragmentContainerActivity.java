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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;

/**
 * @author Hannes Dorfmann
 */
public class SimpleFragmentContainerActivity extends AppCompatActivity {

  private static final String TAG = "Test-Fragment";
  private static Activity currentInstance;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentInstance = this;
    setContentView(R.layout.activity_lifecycle);

    if (savedInstanceState == null) {
      SimpleMviLifecycleFragment f = new SimpleMviLifecycleFragment();
      getSupportFragmentManager().beginTransaction().replace(R.id.container, f, TAG).commitNow();
    }
  }

  public SimpleMviLifecycleFragment getFragment() {
    return (SimpleMviLifecycleFragment) getSupportFragmentManager().findFragmentByTag(TAG);
  }

  public static void pressBackButton() {
    currentInstance.runOnUiThread(new Runnable() {
      @Override public void run() {
        currentInstance.onBackPressed();
      }
    });
  }
}
