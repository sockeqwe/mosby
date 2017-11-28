/*
 * Copyright 2017 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.mvi.integrationtest.backstack;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;
import com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.first.FirstMviFragment;
import com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.first.FirstPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.second.SecondMviFragment;
import com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.second.SecondPresenter;
import java.util.concurrent.atomic.AtomicInteger;

public class BackstackActivity extends AppCompatActivity {

  public static FirstPresenter firstPresenter = new FirstPresenter();
  public static SecondPresenter secondPresenter = new SecondPresenter();
  public static AtomicInteger createFirstPresenterCalls = new AtomicInteger(0);
  public static AtomicInteger createSecondPresenterCalls = new AtomicInteger(0);

  private static BackstackActivity currentInstance;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backstack);

    currentInstance = this;

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragmentContainer, new FirstMviFragment())
          .commit();
    }
  }

  public static void navigateToSecondFragment() {
    currentInstance.runOnUiThread(new Runnable() {
      @Override public void run() {

        currentInstance.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, new SecondMviFragment())
            .addToBackStack(null)
            .commit();
      }
    });
  }

  public static void rotateToLandscape() {
    currentInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  public static void rotateToPortrait() {
    currentInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  public static void pressBackButton() {
    currentInstance.runOnUiThread(new Runnable() {
      @Override public void run() {
        currentInstance.onBackPressed();
      }
    });
  }
}
