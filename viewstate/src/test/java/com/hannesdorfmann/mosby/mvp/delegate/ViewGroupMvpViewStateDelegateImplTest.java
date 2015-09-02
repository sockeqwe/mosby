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

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.mock.PartialViewGroupViewStateCallbackImpl;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleView;
import com.hannesdorfmann.mosby.mvp.delegate.mock.SimpleViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.BuildConfig;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.ViewStateSavedState;
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
public class ViewGroupMvpViewStateDelegateImplTest {

  private SimpleView view;
  private SimpleViewState viewState;
  private MvpViewStateViewGroupDelegateCallback<SimpleView, MvpPresenter<SimpleView>> callback;
  private ViewGroupMvpViewStateDelegateImpl<SimpleView, MvpPresenter<SimpleView>> delegate;
  private MvpPresenter<SimpleView> presenter;

  @Before public void initComponents() {
    view = Mockito.mock(SimpleView.class);

    viewState = Mockito.spy(new SimpleViewState());
    viewState.setStateShowA();
    Assert.assertEquals(SimpleViewState.STATE_A, viewState.state);

    callback = Mockito.mock(PartialViewGroupViewStateCallbackImpl.class);
    presenter = Mockito.mock(MvpPresenter.class);

    Mockito.doCallRealMethod().when(callback).getViewState();
    Mockito.doCallRealMethod().when(callback).setViewState(viewState);
    Mockito.doCallRealMethod().when(callback).getPresenter();
    Mockito.doCallRealMethod().when(callback).setPresenter(presenter);
    Mockito.when(callback.getMvpView()).thenReturn(view);

    delegate = new ViewGroupMvpViewStateDelegateImpl<>(callback);
  }

  @Test public void attachViewNoExistingState() {

    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);

    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.never()).setRestoringViewState(Mockito.anyBoolean());
    Mockito.verify(callback, Mockito.times(1)).setViewState(viewState);
    Mockito.verify(viewState, Mockito.never()).apply(view, callback.shouldInstanceBeRetained());
    Mockito.verify(callback, Mockito.times(1)).onNewViewStateInstance();
  }

  @Test public void restoreStateFromParcelableViewStateOnViewAttached() {

    boolean retaining = false;
    Mockito.when(callback.createPresenter()).thenReturn(presenter);
    Mockito.when(callback.createViewState()).thenReturn(viewState);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);

    Mockito.when(callback.superOnSaveInstanceState()).thenReturn(new Parcelable() {
      @Override public int describeContents() {
        return 0;
      }

      @Override public void writeToParcel(Parcel dest, int flags) {

      }
    });

    // Save the state
    viewState.setStateShowB();
    Parcelable parcelableState = delegate.onSaveInstanceState();

    // Restore the state
    delegate.onRestoreInstanceState(parcelableState);
    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.times(1)).createPresenter();
    Mockito.verify(callback, Mockito.times(1)).setPresenter(presenter);
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, retaining);
    Mockito.verify(callback, Mockito.never()).onNewViewStateInstance();
    Mockito.verify(callback, Mockito.times(1)).onViewStateInstanceRestored(retaining);

    Mockito.verify(view, Mockito.times(1)).showB();
  }

  @Test public void restoreFromRetainingViewState() {

    boolean retaining = true;
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(callback.isRetainInstance()).thenReturn(retaining);

    viewState.setStateShowB();
    delegate.onAttachedToWindow();

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).attachView(view);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(true);
    Mockito.verify(callback, Mockito.times(1)).setRestoringViewState(false);
    Mockito.verify(viewState, Mockito.times(1)).apply(view, retaining);
    Mockito.verify(callback, Mockito.never()).onNewViewStateInstance();
    Mockito.verify(callback, Mockito.times(1)).onViewStateInstanceRestored(retaining);
    Mockito.verify(view, Mockito.times(1)).showB();
  }

  @Test public void detachViewAndSaveToParcelabelViewState() {

    boolean retaining = false;
    callback.setViewState(viewState);

    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);
    Mockito.when(callback.superOnSaveInstanceState()).thenReturn(new Parcelable() {
      @Override public int describeContents() {
        return 0;
      }

      @Override public void writeToParcel(Parcel dest, int flags) {

      }
    });

    delegate.onDetachedFromWindow();
    ViewStateSavedState parcelableState = (ViewStateSavedState) delegate.onSaveInstanceState();

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).detachView(retaining);
    Assert.assertNotNull(parcelableState);
    Assert.assertTrue(parcelableState.getMosbyViewState() == viewState);
  }

  @Test public void detachViewRetainingInstance() {

    boolean retaining = true;
    Mockito.when(callback.getPresenter()).thenReturn(presenter);
    Mockito.when(callback.getViewState()).thenReturn(viewState);
    Mockito.when(callback.shouldInstanceBeRetained()).thenReturn(retaining);

    delegate.onDetachedFromWindow();

    Mockito.verify(callback, Mockito.never()).createPresenter();
    Mockito.verify(presenter, Mockito.times(1)).detachView(retaining);
  }
}
