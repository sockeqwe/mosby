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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.activity;

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class MviLifecycleActivityTest {

  @Rule public ActivityTestRule<MviLifecycleActivity> rule =
      new ActivityTestRule<>(MviLifecycleActivity.class);

  private static LifecycleTestPresenter presenter;

  @Test public void testConfigChange() throws Exception {
    // Context of the app under test.
    MviLifecycleActivity portraitActivity = rule.getActivity();

    presenter = portraitActivity.presenter;
    Assert.assertNotNull(presenter);
    Assert.assertEquals(1, portraitActivity.createPresenterInvokations);
    Assert.assertEquals(1, presenter.attachViewInvokations);
    Assert.assertTrue(presenter.attachedView == portraitActivity);

    Thread.sleep(1000);

    //
    // Screen orientation change
    //
    portraitActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(1000);

    Assert.assertEquals(1, presenter.detachViewInvokations);
    Assert.assertTrue(presenter.onDettachViewRetainInstance);

    Assert.assertEquals(1, MviLifecycleActivity.createPresenterInvokations);
    Assert.assertEquals(2, presenter.attachViewInvokations);
    Assert.assertTrue(presenter.attachedView != portraitActivity);
  }

  @AfterClass public static void checkPresenterNotRetained() {

    // TODO is there a better way to test after onDestroy() has been called?
    Assert.assertNotNull(presenter);
    Assert.assertEquals(2, presenter.detachViewInvokations);
    Assert.assertFalse(presenter.onDettachViewRetainInstance);
  }
}
