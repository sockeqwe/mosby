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
import com.hannesdorfmann.mosby.mvp.delegate.mock.PartialActivityViewStateCallbackImpl;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleView;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.BuildConfig;
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
public class ActivityMvpViewStateDelegateImplTest {

  private SimpleView view;
  private SimpleViewState viewState;
  private ActivityMvpViewStateDelegateCallback<SimpleView, MvpPresenter<SimpleView>> callback;
  private ActivityMvpDelegateImpl<SimpleView, MvpPresenter<SimpleView>> delegate;
  private MvpPresenter<SimpleView> presenter;

  @Before public void initComponents() {
    view = Mockito.mock(SimpleView.class);

    viewState = Mockito.spy(new SimpleViewState());
    viewState.setStateShowA();
    Assert.assertEquals(SimpleViewState.STATE_A, viewState.state);

    callback = Mockito.mock(PartialActivityViewStateCallbackImpl.class);
    presenter = Mockito.mock(MvpPresenter.class);

    Mockito.doCallRealMethod().when(callback).getViewState();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.when(callback.getMvpView()).thenReturn(view);

    delegate = new ActivityMvpViewStateDelegateImpl<>(callback);
  }

  private void startActivity(Bundle bundle) {
    delegate.onCreate(bundle);
    delegate.onContentChanged();
    delegate.onPostCreate(bundle);
    delegate.onStart();
    delegate.onResume();
  }

  private void finishActivity(Bundle bundle) {
    delegate.onPause();
    delegate.onSaveInstanceState(bundle);
    delegate.onStop();
    delegate.onDestroy();
    delegate.onRestart();
  }

  @Test public void startActivityNullBundle() {

    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);

    startActivity(null);

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.never()).setRestoringViewState(Mockito.anyBoolean());
    Mockito.verify(callback, Mockito.times(1)).setViewState(viewState);
    Mockito.verify(viewState, Mockito.never()).apply(view, callback.isRetainInstance());
    Mockito.verify(callback, Mockito.times(1)).onNewViewStateInstance();
  }

  @Test public void restoreFromBundleOnActivityStart() {

    boolean retaining = false;
    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);

    Bundle bundle = new Bundle();
    viewState.setStateShowB();
    viewState.saveInstanceState(bundle);

    startActivity(bundle);

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(callback, Mockito.atLeastOnce()).setViewState(viewState);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, retaining);
    Mockito.verify(callback, Mockito.never()).onNewViewStateInstance();
    Mockito.verify(callback, Mockito.times(1)).onViewStateInstanceRestored(retaining);

    Mockito.verify(view, Mockito.times(1)).showB();
  }

  @Test public void restoreFromRetainingViewState() {

    ActivityMvpViewStateNonConfigurationInstances nci =
        new ActivityMvpViewStateNonConfigurationInstances(presenter, viewState, null);

    boolean retaining = true;
    Mockito.when(callback.getLastCustomNonConfigurationInstance()).thenReturn(nci);
    Mockito.when(callback.isRetainInstance()).thenReturn(retaining);

    viewState.setStateShowB();
    startActivity(null);

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, retaining);
    Mockito.verify(callback, Mockito.never()).onNewViewStateInstance();
    Mockito.verify(callback, Mockito.times(1)).onViewStateInstanceRestored(retaining);
    Mockito.verify(view, Mockito.times(1)).showB();
  }

  @Test public void finishActivityAndSaveStateToBundle() {

    boolean retaining = false;
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);

    Bundle bundle = new Bundle();
    finishActivity(bundle);

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).detachView(retaining);
    Mockito.verify(viewState, Mockito.times(1)).saveInstanceState(bundle);
  }

  @Test public void finishActivityRetaining() {

    boolean retaining = true;
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);

    Bundle bundle = new Bundle();
    finishActivity(bundle);

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).detachView(retaining);
  }

  @Test public void respectRetainingInstanceFlag() {
    // Retaining instance
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(true);
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);

    Assert.assertTrue(presenter == callback.getPresenter());
    Assert.assertTrue(viewState == callback.getViewState());

    ActivityMvpViewStateNonConfigurationInstances nci =
        (ActivityMvpViewStateNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();

    Assert.assertNotNull(nci);
    Assert.assertTrue(nci.presenter == presenter);
    Assert.assertTrue(nci.viewState == viewState);

    // Not retaining instance
    Object customNonConfig = new Object();
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance())
        .thenReturn(customNonConfig);

    nci =
        (ActivityMvpViewStateNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();

    Assert.assertNotNull(nci);
    Assert.assertNull(nci.presenter);
    Assert.assertNull(nci.viewState);
    Assert.assertTrue(nci.nonMosbyCustomConfigurationInstance == customNonConfig);

    // Nothing to retain --> should be null
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance()).thenReturn(null);

    nci =
        (ActivityMvpViewStateNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();
    Assert.assertNull(nci);
  }
}
