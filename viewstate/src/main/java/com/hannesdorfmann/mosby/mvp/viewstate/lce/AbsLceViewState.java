package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * @author Hannes Dorfmann
 */
public class AbsLceViewState<D> implements ParcelableViewState<D> {

  public static final String KEY_BUNDLE_VIEW_STATE =
      "com.hannesdorfmann.mosby.mvp.viewstate.ViewState.bundlekey";

  /**
   * Used as {@link #currentViewState} to indicate that loading is currently displayed on screen
   */
  protected static final int STATE_SHOW_LOADING = 0;

  /**
   * Used as {@link #currentViewState} to indicate that the content is currently displayed on
   * screen
   */
  protected static final int STATE_SHOW_CONTENT = 1;

  /**
   * Used as {@link #currentViewState} to indicate that the error is currently displayed on screen
   */
  protected static final int STATE_SHOW_ERROR = -1;

  /**
   * The current viewstate. Used to identify if the view is/was showing loading, error, or content.
   */
  protected int currentViewState;

  protected boolean pullToRefresh;

  protected Exception exception;
  protected D loadedData;

  /**
   * Saves this ViewState to the outgoing bundle.
   * This will typically be called in {@link android.app.Activity#onSaveInstanceState(Bundle)}
   * or in  {@link android.app.Fragment#onSaveInstanceState(Bundle)}  with the according bundle as
   * argument
   */
  public void saveInstanceState(Bundle out) {
    out.putParcelable(KEY_BUNDLE_VIEW_STATE, this);
  }

  /**
   * This static method reads the bundle with the saved instance state and restores the ViewState
   * from Bundles data
   */
  public static <D> ViewState<D> restoreInstanceState(Bundle in) {
    if (in == null) {
      return null;
    }

    return (ViewState<D>) in.getParcelable(KEY_BUNDLE_VIEW_STATE);
  }

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
    currentViewState = STATE_SHOW_CONTENT;
    this.loadedData = loadedData;
    exception = null;
  }

  public void setStateShowError(Exception e, boolean pullToRefresh) {
    currentViewState = STATE_SHOW_ERROR;
    exception = e;
    this.pullToRefresh = pullToRefresh;
    if (!pullToRefresh){
      loadedData = null;
    }
    // else, dont clear loaded data, because of pull to refresh where previous data may be displayed while showing error
  }

  public void setStateShowLoading(boolean pullToRefresh) {
    currentViewState = STATE_SHOW_LOADING;
    this.pullToRefresh = pullToRefresh;
    exception = null;

    if (!pullToRefresh){
      loadedData = null;
    }
    // else, dont clear loaded data, because of pull to refresh where previous data may be displayed while showing error
  }

  public boolean wasShowingError() {
    return currentViewState == STATE_SHOW_ERROR;
  }

  public boolean wasShowingLoading() {
    return currentViewState == STATE_SHOW_LOADING;
  }

  public boolean wasShowingContent() {
    return currentViewState == STATE_SHOW_CONTENT;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {

    dest.writeInt(currentViewState);

    // PullToRefresh
    writeBoolean(dest, pullToRefresh);

    // write exception
    dest.writeSerializable(exception);

    // content will be written in subclass
  }

  protected void readFromParcel(Parcel in) {
    currentViewState = in.readInt();

    // Pull To Refresh
    pullToRefresh = readBoolean(in);

    // content will be read in subclass
  }

  protected void writeBoolean(Parcel dest, boolean b) {
    dest.writeByte((byte) (b ? 1 : 0));
  }

  protected boolean readBoolean(Parcel p) {
    return p.readByte() == (byte) 1;
  }
}
