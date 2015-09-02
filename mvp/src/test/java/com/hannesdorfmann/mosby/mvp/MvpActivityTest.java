/*
 *  Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.mvp;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class MvpActivityTest {

  private MvpActivity<MvpView, MvpPresenter<MvpView>> activity;

  @Before public void init() {
    activity = Mockito.mock(MvpActivity.class);
    Mockito.doCallRealMethod().when(activity).setRetainInstance(Mockito.anyBoolean());
    Mockito.doCallRealMethod().when(activity).isRetainInstance();
    Mockito.doCallRealMethod().when(activity).shouldInstanceBeRetained();
  }

  @Test public void retainingAndNotChangingConfig() {
    activity.setRetainInstance(true);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(false);
    Assert.assertFalse(activity.shouldInstanceBeRetained());
  }

  @Test public void retainingAndChangingConfig() {
    activity.setRetainInstance(true);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(true);
    Assert.assertTrue(activity.shouldInstanceBeRetained());
  }

  @Test public void notRetainingAndNotChangingConfig() {
    activity.setRetainInstance(false);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(false);
    Assert.assertFalse(activity.shouldInstanceBeRetained());
  }

  @Test public void notRetainingAndChangingConfig() {
    activity.setRetainInstance(false);
    Mockito.when(activity.isChangingConfigurations()).thenReturn(true);
    Assert.assertFalse(activity.shouldInstanceBeRetained());
  }
}
