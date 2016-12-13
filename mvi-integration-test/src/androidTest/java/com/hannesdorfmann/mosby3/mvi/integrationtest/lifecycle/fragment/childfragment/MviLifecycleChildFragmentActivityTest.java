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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.fragment.childfragment;

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class MviLifecycleChildFragmentActivityTest {

  @Rule public ActivityTestRule<MviLifecycleChildFragmentActivity> rule =
      new ActivityTestRule<>(MviLifecycleChildFragmentActivity.class);

  private static LifecycleTestPresenter activityPresenter;
  private static LifecycleTestPresenter fragmentPresenter;
  private static LifecycleTestPresenter childFragmentPresenter;

  @Test public void testConfigChange() throws Exception {
    // Context of the app under test.
    MviLifecycleChildFragmentActivity activity = rule.getActivity();

    activityPresenter = activity.presenter;
    Assert.assertNotNull(activityPresenter);
    Assert.assertEquals(1, MviLifecycleChildFragmentActivity.createPresenterInvokations);
    Assert.assertEquals(1, activityPresenter.attachViewInvokations);
    Assert.assertTrue(activityPresenter.attachedView == activity);

    ContainerMviLifecycleFragment fragment = activity.getFragment();
    fragmentPresenter = fragment.presenter;
    Assert.assertNotNull(fragmentPresenter);
    Assert.assertEquals(1, ContainerMviLifecycleFragment.createPresenterInvokations);
    Assert.assertEquals(1, fragmentPresenter.attachViewInvokations);
    Assert.assertTrue(fragmentPresenter.attachedView == fragment);


    MviLifecycleChildFragment childFragment = fragment.getChildFragment();
    childFragmentPresenter = fragment.presenter;
    Assert.assertNotNull(childFragment);
    Assert.assertEquals(1, MviLifecycleChildFragment.createPresenterInvokations);
    Assert.assertEquals(1, childFragmentPresenter.attachViewInvokations);
    Assert.assertTrue(childFragmentPresenter.attachedView == fragment);

    Thread.sleep(1000);

    //
    // Screen orientation change
    //
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(1000);

    Assert.assertEquals(1, activityPresenter.detachViewInvokations);
    Assert.assertTrue(activityPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, MviLifecycleChildFragmentActivity.createPresenterInvokations);
    Assert.assertEquals(2, activityPresenter.attachViewInvokations);
    Assert.assertNotNull(activityPresenter.attachedView);
    Assert.assertTrue(activityPresenter.attachedView != activity);

    Assert.assertEquals(1, fragmentPresenter.detachViewInvokations);
    Assert.assertTrue(fragmentPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, ContainerMviLifecycleFragment.createPresenterInvokations);
    Assert.assertEquals(2, fragmentPresenter.attachViewInvokations);
    Assert.assertNotNull(fragmentPresenter.attachedView);
    Assert.assertTrue(fragmentPresenter.attachedView != fragment);

    Assert.assertEquals(1, childFragmentPresenter.detachViewInvokations);
    Assert.assertTrue(childFragmentPresenter.onDettachViewRetainInstance);
    Assert.assertEquals(1, MviLifecycleChildFragment.createPresenterInvokations);
    Assert.assertEquals(2, childFragmentPresenter.attachViewInvokations);
    Assert.assertNotNull(childFragmentPresenter.attachedView);
    Assert.assertTrue(childFragmentPresenter.attachedView != fragment);
  }

  @AfterClass public static void checkPresenterNotRetained() {

    // TODO is there a better way to test after onDestroy() has been called?
    Assert.assertNotNull(activityPresenter);
    Assert.assertEquals(2, activityPresenter.detachViewInvokations);
    Assert.assertFalse(activityPresenter.onDettachViewRetainInstance);

    Assert.assertNotNull(fragmentPresenter);
    Assert.assertEquals(2, fragmentPresenter.detachViewInvokations);
    Assert.assertFalse(fragmentPresenter.onDettachViewRetainInstance);


    Assert.assertNotNull(childFragmentPresenter);
    Assert.assertEquals(2, childFragmentPresenter.detachViewInvokations);
    Assert.assertFalse(childFragmentPresenter.onDettachViewRetainInstance);
  }
}
