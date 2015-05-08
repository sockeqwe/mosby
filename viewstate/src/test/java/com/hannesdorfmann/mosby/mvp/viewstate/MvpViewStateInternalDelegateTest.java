package com.hannesdorfmann.mosby.mvp.viewstate;

import org.junit.Ignore;

/**
 * @author Hannes Dorfmann
 */
// @RunWith(RobolectricTestRunner.class) @Config(manifest = Config.NONE)
  @Ignore
public class MvpViewStateInternalDelegateTest {

  // TODO rewrite test

/*
  interface ParcelableDummyView extends MvpLceView<ParcelabledDummyData>,
      MvpViewStateDelegateCallback<ParcelableDummyView, ParcelableDummyPresenter> {

  }

  interface ParcelableDummyPresenter extends MvpPresenter<ParcelableDummyView> {
  }


  @Test(expected = NullPointerException.class) public void failBecauseViewStateNull() {
    ParcelableDummyView view = mock(ParcelableDummyView.class);
    MvpViewStateDelegateCallback delegateCallback = mock(MvpViewStateDelegateCallback.class);
    new MvpViewStateInternalDelegate(delegateCallback).createOrRestoreViewState(new Bundle());
  }

  @Test public void testCreateNew() {

    final ParcelableDataLceViewState viewState =
        new ParcelableDataLceViewState<ParcelabledDummyData, MvpLceView<ParcelabledDummyData>>();

    // Setup mock
    final MvpViewStateDelegateCallback<ParcelableDummyView, ParcelableDummyPresenter> callback = mock(MvpViewStateDelegateCallback.class);
    when(callback.getMvpView()).thenReturn(callback);
    when(callback.createViewState()).thenReturn(viewState);
    when(callback.getViewState()).thenAnswer(new Answer<ViewState>() {
      private int invocationCount = 0;

      @Override public ViewState answer(InvocationOnMock invocation) throws Throwable {
        return invocationCount++ == 0 ? null : viewState;
      }
    });

    MvpViewStateInternalDelegate internal = null;
    // Make call
    internal = new MvpViewStateInternalDelegate(callback);

    boolean restored = internal.createOrRestoreViewState(null);
    boolean applied = internal.applyViewState();

    // Check if new
    verify(callback, times(1)).onNewViewStateInstance();
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
    MvpViewStateInternalDelegate manager = new MvpViewStateInternalDelegate(view);
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
    MvpViewStateInternalDelegate manager = new MvpViewStateInternalDelegate(view);
    manager.saveViewState(bundle);

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
  */
}
