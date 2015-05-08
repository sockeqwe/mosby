package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.data.ParcelabledDummyData;
import com.hannesdorfmann.mosby.mvp.delegate.MvpViewStateInternalDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.ViewStateDelegateCallback;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.ParcelableDataLceViewState;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricTestRunner.class) @Config(manifest = Config.NONE)
public class ViewStateManagerTest {

  interface ParcelableDummyView
      extends MvpLceView<ParcelabledDummyData>,
      ViewStateDelegateCallback<MvpLceView<ParcelabledDummyData>> {

  }

  @Test(expected = NullPointerException.class) public void viewStateSupportIsNull() {
    new MvpViewStateInternalDelegate(null, mock(MvpView.class));
  }

  @Test(expected = NullPointerException.class) public void viewIsNull() {
    MvpViewStateInternalDelegate
        vs = new MvpViewStateInternalDelegate(mock(ViewStateDelegateCallback.class), null);
  }

  @Test(expected = NullPointerException.class) public void failBecauseViewStateNull() {
    ParcelableDummyView view = mock(ParcelableDummyView.class);
    new MvpViewStateInternalDelegate(view, view).createOrRestoreViewState(new Bundle());
  }

  @Test public void testCreateNew() {

    final ParcelableDataLceViewState viewState =
        new ParcelableDataLceViewState<ParcelabledDummyData, MvpLceView<ParcelabledDummyData>>();

    // Setup mock
    final ParcelableDummyView view = mock(ParcelableDummyView.class);
    when(view.createViewState()).thenReturn(viewState);
    when(view.getViewState()).thenAnswer(new Answer<ViewState>() {
      private int invocationCount = 0;

      @Override public ViewState answer(InvocationOnMock invocation) throws Throwable {
        return invocationCount++ == 0 ? null : viewState;
      }
    });

    MvpViewStateInternalDelegate manager = null;
    // Make call
    manager = new MvpViewStateInternalDelegate(view, view);

    boolean restored = manager.createOrRestoreViewState(null);
    boolean applied = manager.applyViewState();

    // Check if new
    verify(view, times(1)).onNewViewStateInstance();
    Assert.assertFalse(restored);
    Assert.assertFalse(applied);
  }

  @Test public void testRestoreAlreadyPresent() {

    final ParcelabledDummyData data = new ParcelabledDummyData();

    final ParcelableDataLceViewState viewState =
        new ParcelableDataLceViewState<ParcelabledDummyData, MvpLceView<ParcelabledDummyData>>();

    viewState.setStateShowContent(data);

    // Setup mock
    final ParcelableDummyView view = mock(ParcelableDummyView.class);
    when(view.createViewState()).thenReturn(viewState);
    when(view.getViewState()).thenReturn(viewState);

    // Make call
    MvpViewStateInternalDelegate manager = new MvpViewStateInternalDelegate(view, view);
    boolean restored = manager.createOrRestoreViewState(null);
    boolean applied = manager.applyViewState();

    // Check if new
    verify(view, times(1)).onViewStateInstanceRestored(true);
    verify(view, times(1)).showContent();
    verify(view, times(1)).setData(data);

    Assert.assertTrue(restored);
    Assert.assertTrue(applied);
  }

  @Test public void testSaveAndRestoreFromBundle() {

    final ParcelabledDummyData data = new ParcelabledDummyData();

    final ParcelableDataLceViewState viewState =
        new ParcelableDataLceViewState<ParcelabledDummyData, MvpLceView<ParcelabledDummyData>>();

    // Setup mock
    final ParcelableDummyView view = mock(ParcelableDummyView.class);
    when(view.createViewState()).thenReturn(viewState);
    when(view.getViewState()).thenAnswer(new Answer<ViewState>() {
      private int invocationCount = 0;

      @Override public ViewState answer(InvocationOnMock invocation) throws Throwable {
        return invocationCount++ == 1 ? null : viewState;
      }
    });

    viewState.setStateShowContent(data);

    Bundle bundle = new Bundle();
    MvpViewStateInternalDelegate manager = new MvpViewStateInternalDelegate(view, view);
    manager.saveViewState(bundle, false);

    // Make call
    boolean restored = manager.createOrRestoreViewState(bundle);
    boolean applied = manager.applyViewState();

    // Check if new
    verify(view, times(1)).onViewStateInstanceRestored(false);
    verify(view, times(1)).showContent();
    verify(view, times(1)).setData(data);
    Assert.assertTrue(restored);
    Assert.assertTrue(applied);
  }
}
