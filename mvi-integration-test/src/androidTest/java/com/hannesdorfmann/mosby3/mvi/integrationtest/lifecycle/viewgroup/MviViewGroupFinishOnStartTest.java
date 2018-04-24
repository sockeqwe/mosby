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

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MviViewGroupFinishOnStartTest {

    @Rule
    public ActivityTestRule<ViewGroupFinishOnStartContainerActivity> rule =
            new ActivityTestRule<>(ViewGroupFinishOnStartContainerActivity.class);

    @Test
    public void screenOrientationChange() throws Exception {
        // Context of the app under test.
        ViewGroupFinishOnStartContainerActivity portraitActivity = rule.getActivity();
        ViewGroupMviDelegateImpl.DEBUG = true;
        ViewGroupFinishOnCreateLayout layout = portraitActivity.getLayout();

        Thread.sleep(2000);

        Assert.assertNull(layout.presenter);
        Assert.assertEquals(0, layout.createPresenterInvocations);
    }
}
