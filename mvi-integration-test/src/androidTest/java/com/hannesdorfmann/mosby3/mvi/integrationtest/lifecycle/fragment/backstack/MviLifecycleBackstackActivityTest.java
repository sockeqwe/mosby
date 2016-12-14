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

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class MviLifecycleBackstackActivityTest {

  @Rule public ActivityTestRule<MviLifecycleBackstackActivity> rule =
      new ActivityTestRule<>(MviLifecycleBackstackActivity.class);

  private static LifecycleTestPresenter activityPresenter;
  private static LifecycleTestPresenter firstFragmentPresenter;
  private static LifecycleTestPresenter secondFragmentPresenter;

  @Test public void screenOrientationChangesWithFragmentsOnBackstack() throws Throwable {
    // Context of the app under test.
    MviLifecycleBackstackActivity activity = rule.getActivity();
    Thread.sleep(200); // Some time for the first transaction

    activityPresenter = activity.presenter;
    Assert.assertNotNull(activityPresenter);
    Assert.assertEquals(1, MviLifecycleBackstackActivity.createPresenterInvokations);
    Assert.assertEquals(1, activityPresenter.attachViewInvokations);
    Assert.assertTrue(activityPresenter.attachedView == activity);

    FirstBackstackMviLifecycleFragment firstFragment = activity.getFirstFragment();
    firstFragmentPresenter = firstFragment.presenter;
    Assert.assertNotNull(firstFragmentPresenter);
    Assert.assertEquals(1, FirstBackstackMviLifecycleFragment.createPresenterInvokations);
    Assert.assertEquals(1, firstFragmentPresenter.attachViewInvokations);
    Assert.assertTrue(firstFragmentPresenter.attachedView == firstFragment);

    //
    // Put second fragment on backstack
    //
    Thread.sleep(500);
    activity.putSecondFragmentOnTopOfBackStack();
    Thread.sleep(2000);

    // Assert FirstFragment is detached
    Assert.assertEquals(1, firstFragmentPresenter.detachViewInvokations);
    Assert.assertTrue(firstFragmentPresenter.onDettachViewRetainInstance);

    SecondBackstackMviLifecycleFragment secondFragment = activity.getSecondFragment();
    secondFragmentPresenter = secondFragment.presenter;
    Assert.assertNotNull(secondFragment);
    Assert.assertEquals(1, SecondBackstackMviLifecycleFragment.createPresenterInvokations);
    Assert.assertEquals(1, secondFragmentPresenter.attachViewInvokations);
    Assert.assertTrue(secondFragmentPresenter.attachedView == secondFragment);

    //
    // Screen orientation change
    //
    Thread.sleep(200);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(1000);

    Assert.assertEquals(1, activityPresenter.detachViewInvokations);
    Assert.assertTrue(activityPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, activity.createPresenterInvokations);
    Assert.assertEquals(2, activityPresenter.attachViewInvokations);
    Assert.assertNotNull(activityPresenter.attachedView);
    Assert.assertTrue(activityPresenter.attachedView != activity);
    Assert.assertTrue(activityPresenter.attachedView instanceof MviLifecycleBackstackActivity);

    Assert.assertEquals(1, secondFragmentPresenter.detachViewInvokations);
    Assert.assertTrue(secondFragmentPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, secondFragment.createPresenterInvokations);
    Assert.assertEquals(2, secondFragmentPresenter.attachViewInvokations);
    Assert.assertNotNull(secondFragmentPresenter.attachedView);
    Assert.assertTrue(secondFragmentPresenter.attachedView != firstFragment);
    Assert.assertTrue(
        secondFragmentPresenter.attachedView instanceof SecondBackstackMviLifecycleFragment);

    //
    // press back button --> first Fragment should appear
    //
    Thread.sleep(1000);
    rule.runOnUiThread(new Runnable() {
      @Override public void run() {
        MviLifecycleBackstackActivity.pressBackButton();
      }
    });
    Thread.sleep(1000);

    Assert.assertEquals(2, secondFragmentPresenter.detachViewInvokations);
    Assert.assertFalse(secondFragmentPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, SecondBackstackMviLifecycleFragment.createPresenterInvokations);

    // First fragment reattached
    Assert.assertEquals(1, firstFragment.createPresenterInvokations);
    Assert.assertEquals(2, firstFragmentPresenter.attachViewInvokations);
    Assert.assertNotNull(firstFragmentPresenter.attachedView);
    Assert.assertTrue(firstFragmentPresenter.attachedView != firstFragment);
    Assert.assertTrue(
        firstFragmentPresenter.attachedView instanceof FirstBackstackMviLifecycleFragment);
  }

  @AfterClass public static void checkPresenterNotRetained() {

    // TODO is there a better way to test after onDestroy() has been called?
    Assert.assertNotNull(activityPresenter);
    Assert.assertEquals(2, activityPresenter.detachViewInvokations);
    Assert.assertFalse(activityPresenter.onDettachViewRetainInstance);

    Assert.assertNotNull(firstFragmentPresenter);
    Assert.assertEquals(2, firstFragmentPresenter.detachViewInvokations);
    Assert.assertFalse(firstFragmentPresenter.onDettachViewRetainInstance);
  }
}
