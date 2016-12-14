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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.fragment.backstack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestView;

public class MviLifecycleBackstackActivity
    extends MviActivity<LifecycleTestView, LifecycleTestPresenter> implements LifecycleTestView {

  private static MviLifecycleBackstackActivity currentActivity;
  private static final String FIRST_TAG = "firstFragment";
  private static final String SECOND_TAG = "secondFragment";
  public LifecycleTestPresenter presenter;
  public static int createPresenterInvokations = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    currentActivity = this;
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backstack_mvi_container);
    Log.d(getClass().getSimpleName(), "onCreate() " + this);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragmentContainer, new FirstBackstackMviLifecycleFragment(), FIRST_TAG)
          .addToBackStack("first")
          .commit();
    }
  }

  public FirstBackstackMviLifecycleFragment getFirstFragment() {
    return (FirstBackstackMviLifecycleFragment) getSupportFragmentManager().findFragmentByTag(
        FIRST_TAG);
  }

  public SecondBackstackMviLifecycleFragment getSecondFragment() {
    return (SecondBackstackMviLifecycleFragment) getSupportFragmentManager().findFragmentByTag(
        SECOND_TAG);
  }

  public void putSecondFragmentOnTopOfBackStack() {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragmentContainer, new SecondBackstackMviLifecycleFragment(), SECOND_TAG)
        .addToBackStack("second")
        .commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.d(getClass().getSimpleName(), "onDestroy() " + this);
    currentActivity = null;
  }

  @NonNull @Override public LifecycleTestPresenter createPresenter() {
    createPresenterInvokations++;
    presenter = new LifecycleTestPresenter();
    Log.d(getClass().getSimpleName(), "createPresenter() " + this + " " + presenter);
    return presenter;
  }

  public static void pressBackButton() {
    currentActivity.onBackPressed();
  }
}
