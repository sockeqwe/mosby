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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.viewgroup;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.hannesdorfmann.mosby3.ViewGroupMviDelegateImpl;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) public class MviViewGroupContainerActivityTest2 {

  @Rule public ActivityTestRule<MviViewGroupContainerActivity> rule =
      new ActivityTestRule<>(MviViewGroupContainerActivity.class);

  private static LifecycleTestPresenter presenter;


  @Test public void removeMviViewGroupManually() throws Exception {
    // Context of the app under test.
    MviViewGroupContainerActivity portraitActivity = rule.getActivity();
    ViewGroupMviDelegateImpl.DEBUG = true;

    TestMviFrameLayout viewGroup = MviViewGroupContainerActivity.getMviViewGroup();

    presenter = viewGroup.presenter;
    Assert.assertNotNull(presenter);
    Assert.assertEquals(1, viewGroup.createPresenterInvocations);
    Assert.assertEquals(1, presenter.attachViewInvokations);
    Assert.assertEquals(1, presenter.bindIntentInvocations);
    Assert.assertTrue(presenter.attachedView == viewGroup);

    Thread.sleep(1000);

    //
    // Screen orientation change
    //
    MviViewGroupContainerActivity.removeMviViewGroup();
    Thread.sleep(1000);

    Assert.assertEquals(1, presenter.detachViewInvokations);
    Assert.assertEquals(1, presenter.unbindIntentInvocations);
    Assert.assertEquals(1, presenter.destoryInvoations);
    Assert.assertEquals(1, viewGroup.createPresenterInvocations);
    Assert.assertEquals(1, presenter.attachViewInvokations);
    Assert.assertNull(presenter.attachedView);

  }
}
