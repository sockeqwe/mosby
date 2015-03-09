package com.hannesdorfmann.mosby.mvp.viewstate.lce;

/**
 * This kind of {@link LceViewState} can be used with <b></b>Fragments that have set
 * setRetainInstance(true); <b/>, because that allows to store / restore any kind of data along
 * orientation changes. So this ViewState will not be saved into the Bundle of saveInstanceState().
 * This ViewState will be stored and restored directly by the Fragment itself by setting
 * Fragment.setRetainInstance(true);
 *
 * <p>
 * Can be used for <b>Fragments only</b>.
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class RetainingFragmentLceViewState<D> implements LceViewState<D> {

  protected int currentViewState;
  protected boolean pullToRefresh;
  protected Exception exception;
  protected D loadedData;

  public boolean isPullToRefresh() {
    return pullToRefresh;
  }

  public Exception getException() {
    return exception;
  }

  public D getLoadedData() {
    return loadedData;
  }

  public void setStateShowContent(D loadedData) {
    currentViewState = AbsLceViewState.STATE_SHOW_CONTENT;
    this.loadedData = loadedData;
    exception = null;
  }

  public void setStateShowError(Exception e, boolean pullToRefresh) {
    currentViewState = AbsLceViewState.STATE_SHOW_ERROR;
    exception = e;
    this.pullToRefresh = pullToRefresh;
    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, dont clear loaded data, because of pull to refresh where previous data may
    // be displayed while showing error
  }

  public void setStateShowLoading(boolean pullToRefresh) {
    currentViewState = AbsLceViewState.STATE_SHOW_LOADING;
    this.pullToRefresh = pullToRefresh;
    exception = null;

    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, don't clear loaded data, because of pull to refresh where previous data
    // may be displayed while showing error
  }

  public boolean wasShowingError() {
    return currentViewState == AbsLceViewState.STATE_SHOW_ERROR;
  }

  public boolean wasShowingLoading() {
    return currentViewState == AbsLceViewState.STATE_SHOW_LOADING;
  }

  public boolean wasShowingContent() {
    return currentViewState == AbsLceViewState.STATE_SHOW_CONTENT;
  }
}
