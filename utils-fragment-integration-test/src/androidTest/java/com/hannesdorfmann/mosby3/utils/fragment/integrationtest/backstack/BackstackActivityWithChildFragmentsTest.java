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
@RunWith(AndroidJUnit4.class) public class BackstackActivityWithChildFragmentsTest {

  @Rule public ActivityTestRule<BackstackActivityWithChildFragments> rule =
      new ActivityTestRule<>(BackstackActivityWithChildFragments.class);

  @Test public void fragmentsOnBackstack() throws Exception {
    // Context of the app under test.
    BackstackActivityWithChildFragments activity = rule.getActivity();
    Thread.sleep(1000);

    FragmentOnBackstack fragmentOnBackstack;
    ChildFragmentOnBackstack childFragmentOnBackstack;
    ChildChildFragmentOnBackstack childChildFragmentOnBackstack;

    SimpleFragmentNotOnBackstack simpleFragmentNotOnBackstack;
    SimpleChildFragmentNotOnBackstack simpleChildFragmentNotOnBackstack;
    SimpleChildChildFragmentNotOnBackstack simpleChildChildFragmentNotOnBackstack;

    fragmentOnBackstack = activity.getFragmentOnBackstack();
    Assert.assertNotNull(fragmentOnBackstack);
    childFragmentOnBackstack = fragmentOnBackstack.getChilFragment();
    Assert.assertNotNull(childFragmentOnBackstack);
    childChildFragmentOnBackstack = childFragmentOnBackstack.getChildFragment();
    Assert.assertNotNull(childChildFragmentOnBackstack);

    simpleFragmentNotOnBackstack = activity.getFragmentNotOnBackstack();
    Assert.assertNotNull(simpleFragmentNotOnBackstack);
    simpleChildFragmentNotOnBackstack = simpleFragmentNotOnBackstack.getChilFragment();
    Assert.assertNotNull(simpleChildFragmentNotOnBackstack);
    simpleChildChildFragmentNotOnBackstack = simpleChildFragmentNotOnBackstack.getChildFragment();
    Assert.assertNotNull(simpleChildChildFragmentNotOnBackstack);

    Thread.sleep(1000);
    //
    // Screen orientation change
    //
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    // Will call onStop() where we set the onbackStack flag
    Thread.sleep(1000);

    Assert.assertTrue(fragmentOnBackstack.onBackStack);
    Assert.assertTrue(childFragmentOnBackstack.onBackStack);
    Assert.assertTrue(childChildFragmentOnBackstack.onBackStack);

    Assert.assertFalse(simpleFragmentNotOnBackstack.onBackStack);
    Assert.assertFalse(simpleChildFragmentNotOnBackstack.onBackStack);
    Assert.assertFalse(simpleChildChildFragmentNotOnBackstack.onBackStack);
  }
}
