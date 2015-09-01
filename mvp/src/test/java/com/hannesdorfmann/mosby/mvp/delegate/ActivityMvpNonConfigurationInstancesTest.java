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

package com.hannesdorfmann.mosby.mvp.delegate;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class ActivityMvpNonConfigurationInstancesTest {

  @Test public void constructor() {

    MvpPresenter p = Mockito.mock(MvpPresenter.class);
    Object customData = new Object();
    ActivityMvpNonConfigurationInstances i =
        new ActivityMvpNonConfigurationInstances(p, customData);

    Assert.assertTrue(i.presenter == p);
    Assert.assertTrue(i.nonMosbyCustomConfigurationInstance == customData);
  }
}
