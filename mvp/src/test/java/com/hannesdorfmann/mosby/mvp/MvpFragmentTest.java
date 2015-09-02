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

import android.support.v4.app.FragmentActivity;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */

@RunWith(PowerMockRunner.class) @PrepareForTest(MvpFragment.class)
public class MvpFragmentTest {

  private MvpFragment<MvpView, MvpPresenter<MvpView>> fragment;
  private FragmentActivity activity;

  @Before public void init() {

    activity = Mockito.mock(FragmentActivity.class);
    fragment = PowerMockito.spy(new MvpFragment<MvpView, MvpPresenter<MvpView>>() {
      @Override public MvpPresenter<MvpView> createPresenter() {
        return null;
      }
    });
  }

  @Test public void retainingAndActivityNull() {
    PowerMockito.when(fragment.getRetainInstance()).thenReturn(true);
    PowerMockito.when(fragment.getActivity()).thenReturn(null);

    Assert.assertFalse(fragment.shouldInstanceBeRetained());
  }

  @Test public void notRetainingAndActivityNull() {
    PowerMockito.when(fragment.getRetainInstance()).thenReturn(false);
    PowerMockito.when(fragment.getActivity()).thenReturn(null);

    Assert.assertFalse(fragment.shouldInstanceBeRetained());
  }

  @Test public void retainingAndNotChangingConfig() {
    PowerMockito.when(fragment.getRetainInstance()).thenReturn(true);
    PowerMockito.when(activity.isChangingConfigurations()).thenReturn(false);

    Assert.assertFalse(fragment.shouldInstanceBeRetained());
  }

  @Test public void retainingAndChangingConfig() {

    PowerMockito.when(fragment.getActivity()).thenReturn(activity);
    PowerMockito.when(fragment.getRetainInstance()).thenReturn(true);
    PowerMockito.when(activity.isChangingConfigurations()).thenReturn(true);

    Assert.assertTrue(fragment.shouldInstanceBeRetained());
  }
}
