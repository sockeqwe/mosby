package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * An LCE (Loading-Content-Error) {@link ViewState} is a viewstate that can save and restore
 * loading, the content, or the error (exception).
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface LceViewState<D> extends ViewState<MvpLceView<D>> {
  /**
   * Used as currentViewState to indicate that loading is currently displayed on screen
   */
  int STATE_SHOW_LOADING = 0;
  /**
   * Used as currentViewState to indicate that the content is currently displayed on
   * screen
   */
  int STATE_SHOW_CONTENT = 1;
  /**
   * Used as currentViewState to indicate that the error is currently displayed on screen
   */
  int STATE_SHOW_ERROR = -1;


  void setStateData(D loadedData);

  void setStateShowContent();

  void setStateShowError(Exception e, boolean pullToRefresh);

  void setStateShowLoading(boolean pullToRefresh);

}
