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

package com.hannesdorfmann.mosby3.mvi.backstack;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.BackstackActivity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class BackstackActivityTest {

  @Rule public ActivityTestRule<BackstackActivity> rule =
      new ActivityTestRule<>(BackstackActivity.class);

  @Test public void testConfigChange() throws Exception {

    // FragmentMviDelegateImpl.DEBUG = true;

    // Context of the app under test.
    BackstackActivity portraitActivity = rule.getActivity();

    Thread.sleep(1000);
    Assert.assertEquals(1, BackstackActivity.createFirstPresenterCalls.get());
    Assert.assertEquals(1, BackstackActivity.firstPresenter.attachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.firstPresenter.bindIntentCalls.get());

    //
    // Screen orientation change
    //
    BackstackActivity.rotateToLandscape();
    Thread.sleep(1000);

    Assert.assertEquals(1, BackstackActivity.firstPresenter.detachViewCalls.get());
    Assert.assertEquals(0, BackstackActivity.firstPresenter.unbindIntentCalls.get());
    Assert.assertEquals(1, BackstackActivity.createFirstPresenterCalls.get());
    Assert.assertEquals(2, BackstackActivity.firstPresenter.attachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.firstPresenter.bindIntentCalls.get());

    //
    // Navigate to next fragment
    //
    Thread.sleep(1000);
    BackstackActivity.navigateToSecondFragment();
    Thread.sleep(1000);

    Assert.assertEquals(2, BackstackActivity.firstPresenter.detachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.firstPresenter.unbindIntentCalls.get());
    Assert.assertEquals(1, BackstackActivity.createFirstPresenterCalls.get());
    Assert.assertEquals(2, BackstackActivity.firstPresenter.attachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.firstPresenter.bindIntentCalls.get());

    //
    // Check Second Fragment
    //
    Assert.assertEquals(1, BackstackActivity.createSecondPresenterCalls.get());
    Assert.assertEquals(1, BackstackActivity.secondPresenter.attachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.secondPresenter.bindIntentCalls.get());

    //
    // Screen orientation change
    //
    BackstackActivity.rotateToPortrait();
    Thread.sleep(1000);

    Assert.assertEquals(1, BackstackActivity.secondPresenter.detachViewCalls.get());
    Assert.assertEquals(0, BackstackActivity.secondPresenter.unbindIntentCalls.get());
    Assert.assertEquals(1, BackstackActivity.createSecondPresenterCalls.get());
    Assert.assertEquals(2, BackstackActivity.secondPresenter.attachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.secondPresenter.bindIntentCalls.get());

    //
    // Press back button
    //
    BackstackActivity.pressBackButton();
    Thread.sleep(1000);
  }

  @AfterClass public static void afterBackButtonPressed() {
    Assert.assertEquals(2, BackstackActivity.secondPresenter.detachViewCalls.get());
    Assert.assertEquals(1, BackstackActivity.secondPresenter.unbindIntentCalls.get());
  }
}
