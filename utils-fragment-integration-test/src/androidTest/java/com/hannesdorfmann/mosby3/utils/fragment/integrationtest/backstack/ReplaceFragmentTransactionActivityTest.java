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

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class) public class ReplaceFragmentTransactionActivityTest {

  @Rule public ActivityTestRule<ReplaceFragmentTransactionActivity> rule =
      new ActivityTestRule<>(ReplaceFragmentTransactionActivity.class);

  @Test public void fragmentsOnBackstack() throws Exception {
    // Context of the app under test.
    ReplaceFragmentTransactionActivity activity = rule.getActivity();

    Thread.sleep(25000);

    //
    // Screen orientation change
    //
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    // Will call onStop() where we set the onbackStack flag
    Thread.sleep(1000);

    Assert.assertEquals(ReplaceFragmentTransactionActivity.FRAGMENTS_COUNT,
        ReplaceTransactionFragment.onBackStackCount);

    Assert.assertEquals(ReplaceFragmentTransactionActivity.FRAGMENTS_COUNT,
        ReplaceTransactionFragment.notOnBackStackCount);
  }
}
