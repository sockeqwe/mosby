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
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class ActivityMvpDelegateImplTest {

  private MvpView view;
  private MvpPresenter<MvpView> presenter;
  private PartialActivityMvpDelegateCallbackImpl callback;
  private ActivityMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate;

  @Before public void initComponents() {
    view = new MvpView() {
    };

    presenter = Mockito.mock(MvpPresenter.class);
    callback = Mockito.mock(PartialActivityMvpDelegateCallbackImpl.class);
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.doCallRealMethod().when(callback).getPresenter();

    Mockito.when(callback.getMvpView()).thenReturn(view);

    delegate = new ActivityMvpDelegateImpl<>(callback);
  }

  @Test public void startActivityNullBundle() {
    testActivityStart(null);
  }

  @Test public void startActivityWithBundle() {
    testActivityStart(new Bundle());
  }

  private void testActivityStart(Bundle bundle) {
    Mockito.when(callback.createPresenter()).thenReturn(presenter);

    startActivity(bundle);

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
  }

  private void startActivity(Bundle bundle) {
    delegate.onCreate(bundle);
    delegate.onContentChanged();
    delegate.onPostCreate(bundle);
    delegate.onStart();
    delegate.onResume();
  }

  @Test public void respectRetainingInstanceFlag() {
    // Retaining instance
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(true);
    Mockito.when(callback.getPresenter()).thenReturn(presenter);

    Assert.assertTrue(presenter == callback.getPresenter());
    ActivityMvpNonConfigurationInstances nci =
        (ActivityMvpNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();

    Assert.assertNotNull(nci);
    Assert.assertTrue(nci.presenter == presenter);

    // Not retaining instance
    Object customNonConfig = new Object();
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance())
        .thenReturn(customNonConfig);

    nci = (ActivityMvpNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();

    Assert.assertNotNull(nci);
    Assert.assertNull(nci.presenter);
    Assert.assertTrue(nci.nonMosbyCustomConfigurationInstance == customNonConfig);

    // Nothing to retain --> should be null
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance()).thenReturn(null);

    nci = (ActivityMvpNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();
    Assert.assertNull(nci);
  }

  @Test public void reuseRetainingPresenter() {

    ActivityMvpNonConfigurationInstances nci =
        new ActivityMvpNonConfigurationInstances(presenter, null);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(true);
    Mockito.when(callback.getLastCustomNonConfigurationInstance()).thenReturn(nci);

    startActivity(null);

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
  }

  @Test public void onRetainCustomNonConfigurationInstance() {
    Object nonMosbyLastNci = new Object();
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(true);
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance())
        .thenReturn(nonMosbyLastNci);

    ActivityMvpNonConfigurationInstances nci =
        (ActivityMvpNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();
    Assert.assertNotNull(nci);
    Assert.assertTrue(nci.presenter == presenter);
    Assert.assertTrue(nci.nonMosbyCustomConfigurationInstance == nonMosbyLastNci);
  }

  @Test public void retainCustomNonMosbyNonConfigInstanceMatchesGetNonMosbyConfigInstance() {

    Object nonMosbyLastNci = new Object();
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(false);
    Mockito.when(callback.onRetainNonMosbyCustomNonConfigurationInstance())
        .thenReturn(nonMosbyLastNci);

    ActivityMvpNonConfigurationInstances nci =
        (ActivityMvpNonConfigurationInstances) delegate.onRetainCustomNonConfigurationInstance();

    Mockito.when(callback.getLastCustomNonConfigurationInstance()).thenReturn(nci);

    Object quriedNci = delegate.getNonMosbyLastCustomNonConfigurationInstance();

    Assert.assertTrue(quriedNci == nonMosbyLastNci);
  }

  @Test public void finishActivityNotRetaining() {
    testFinishActivity(false);
  }

  @Test public void finishActivityIsRetaining() {
    testFinishActivity(true);
  }

  private void testFinishActivity(boolean retainingInstanceState) {
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retainingInstanceState);

    delegate.onPause();
    delegate.onSaveInstanceState(new Bundle());
    delegate.onStop();
    delegate.onDestroy();
    delegate.onRestart();

    Mockito.verify(presenter, Mockito.times(1)).detachView(retainingInstanceState);
  }
}
