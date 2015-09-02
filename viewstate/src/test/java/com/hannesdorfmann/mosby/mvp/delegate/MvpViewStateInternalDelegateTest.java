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

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.delegate.mock.PartialViewStateCallbackImpl;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleView;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.BuildConfig;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class MvpViewStateInternalDelegateTest {

  private SimpleView view;
  private SimpleViewState viewState;
  private BaseMvpViewStateDelegateCallback<SimpleView, MvpPresenter<SimpleView>> callback;
  private MvpViewStateInternalDelegate<SimpleView, MvpPresenter<SimpleView>> delegate;

  @Before public void initComponents() {
    view = Mockito.mock(SimpleView.class);

    viewState = Mockito.spy(new SimpleViewState());
    callback = Mockito.mock(PartialViewStateCallbackImpl.class);

    Mockito.doCallRealMethod().when(callback).getViewState();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.when(callback.getMvpView()).thenReturn(view);

    delegate = new MvpViewStateInternalDelegate<>(callback);
  }

  @Test public void createNewViewState() {

    Mockito.when(callback.createViewState()).thenReturn(viewState);

    boolean viewStateRestored = delegate.createOrRestoreViewState(null);

    Assert.assertFalse(viewStateRestored);
    Mockito.verify(callback, Mockito.times(1)).createViewState();
    Mockito.verify(callback, Mockito.times(1)).setViewState(viewState);
    Mockito.verify(callback, Mockito.never()).setRestoringViewState(Mockito.anyBoolean());
  }

  @Test public void restoreViewStateFromBundle() {

    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.createViewState()).thenReturn(viewState);

    Assert.assertEquals(SimpleViewState.STATE_A, viewState.state);
    viewState.setStateShowB();
    Bundle bundle = new Bundle();

    viewState.saveInstanceState(bundle);

    boolean restored = delegate.createOrRestoreViewState(bundle);
    Assert.assertTrue(restored);
    delegate.applyViewState();

    Mockito.verify(callback, Mockito.atLeast(1)).setViewState(viewState);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, callback.shouldInstanceBeRetained());
    Mockito.verify(view, Mockito.times(1)).showB();
  }

  /**
   * Test if restoring a retaining view state (i.e. retaining fragment, not saved into bundle)
   * works
   * properly
   */
  @Test public void restoreRetainingViewStateWithBundle() {

    // Restore a retaining view state i.e. retaining fragment (not from bundle)

    Mockito.when(callback.isRetainInstance()).thenReturn(true);
    Mockito.when(callback.getViewState()).thenReturn(viewState);

    Assert.assertEquals(SimpleViewState.STATE_A, viewState.state);

    viewState.setStateShowB();
    Bundle bundle = new Bundle();

    boolean restored = delegate.createOrRestoreViewState(bundle);
    Assert.assertTrue(restored);
    delegate.applyViewState();

    Mockito.verify(callback, Mockito.never()).createViewState();
    Mockito.verify(callback, Mockito.never()).setViewState(viewState);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, true);
    Mockito.verify(view, Mockito.times(1)).showB();
  }

  /**
   * Test if restoring a retaining view state (i.e. retaining fragment, not saved into bundle)
   * works
   * properly
   */
  @Test public void restoreRetainingViewStateWithNullBundle() {

    // Restore a retaining view state i.e. retaining fragment (not from bundle)

    Mockito.when(callback.isRetainInstance()).thenReturn(true);
    Mockito.when(callback.getViewState()).thenReturn(viewState);

    Assert.assertEquals(SimpleViewState.STATE_A, viewState.state);

    viewState.setStateShowB();
    Bundle bundle = null;

    boolean restored = delegate.createOrRestoreViewState(bundle);
    Assert.assertTrue(restored);
    delegate.applyViewState();

    Mockito.verify(callback, Mockito.never()).createViewState();
    Mockito.verify(callback, Mockito.never()).setViewState(viewState);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, true);
    Mockito.verify(view, Mockito.times(1)).showB();
  }

  @Test public void saveViewState() {

    Mockito.when(callback.getViewState()).thenReturn(viewState);

    Bundle bundle = new Bundle();
    delegate.saveViewState(bundle);

    Mockito.verify(viewState, Mockito.times(1)).saveInstanceState(bundle);
  }

  @Test public void throwsExceptionIfViewStateFeatureIsSenseless() {

    ViewState vs = new ViewState() {
      @Override public void apply(MvpView view, boolean retained) {

      }
    };

    Mockito.when(callback.getViewState()).thenReturn(vs);
    Mockito.when(callback.isRetainInstance()).thenReturn(false);

    // Test without bundle
    try {
      delegate.saveViewState(null);
      Assert.fail("An exception should be thrown");
    } catch (IllegalStateException e) {
      // Exception is expected
    }

    // Test with bundle
    try {
      delegate.saveViewState(new Bundle());
      Assert.fail("An exception should be thrown");
    } catch (IllegalStateException e) {
      // Exception is expected
    }
  }
}
