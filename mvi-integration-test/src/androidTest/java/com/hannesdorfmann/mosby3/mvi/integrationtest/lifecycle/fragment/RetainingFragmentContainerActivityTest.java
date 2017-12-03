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

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class RetainingFragmentContainerActivityTest {

  @Rule public ActivityTestRule<RetainingFragmentContainerActivity> rule =
      new ActivityTestRule<>(RetainingFragmentContainerActivity.class);

  private static LifecycleTestPresenter presenter;

  @Test public void screenOrientationChange() throws Exception {
    // Context of the app under test.
    RetainingFragmentContainerActivity portraitActivity = rule.getActivity();

    SimpleRetainingMviLifecycleFragment fragment = portraitActivity.getFragment();

    presenter = fragment.presenter;
    Assert.assertNotNull(presenter);
    Assert.assertEquals(1, fragment.createPresenterInvokations);
    Assert.assertEquals(1, presenter.attachViewInvokations);
    Assert.assertEquals(1, presenter.bindIntentInvocations);
    Assert.assertTrue(presenter.attachedView == fragment);

    Thread.sleep(1000);

    //
    // Screen orientation change
    //
    portraitActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(1000);

    Assert.assertEquals(1, presenter.detachViewInvokations);

    Assert.assertEquals(1, fragment.createPresenterInvokations);
    Assert.assertEquals(2, presenter.attachViewInvokations);
    Assert.assertEquals(1, presenter.bindIntentInvocations);
    Assert.assertTrue(presenter.attachedView == fragment);


    //
    // Press back button --> Activity finishes
    //
    RetainingFragmentContainerActivity.pressBackButton();
    Thread.sleep(1000);
  }

  @AfterClass
  public static void checkPresenterNotRetained(){
    Assert.assertNotNull(presenter);
    Assert.assertEquals(2, presenter.detachViewInvokations);
    Assert.assertEquals(1, presenter.destoryInvoations);
    Assert.assertEquals(1, presenter.unbindIntentInvocations);
  }
}
