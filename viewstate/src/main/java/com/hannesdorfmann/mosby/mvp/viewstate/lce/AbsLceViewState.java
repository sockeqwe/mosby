package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;

/**
 * A base view state implementation for {@link LceViewState} (Loading-Content-Error) and {@link
 * ParcelableViewState}. This class can be saved and restored in a bundle. Therefore it can be used
 * for Activities and Fragments.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class AbsLceViewState<D> implements ParcelableViewState, LceViewState<D> {

  public static final String KEY_BUNDLE_VIEW_STATE =
      "com.hannesdorfmann.mosby.mvp.viewstate.ViewState.bundlekey";

  /**
   * The current viewstate. Used to identify if the view is/was showing loading, error, or content.
   */
  protected int currentViewState;
  protected boolean pullToRefresh;
  protected Exception exception;
  protected D loadedData;

  @Override
  public void saveInstanceState(Bundle out) {
    out.putParcelable(KEY_BUNDLE_VIEW_STATE, this);
  }

  @Override
  public void restoreInstanceState(Bundle in) {
    if (in == null) {
      return;
    }

    AbsLceViewState<D> tmp = (AbsLceViewState<D>) in.getParcelable(KEY_BUNDLE_VIEW_STATE);
    this.loadedData = tmp.loadedData;
    this.currentViewState = tmp.currentViewState;
    this.exception = tmp.exception;
    this.pullToRefresh = tmp.pullToRefresh;
  }

  @Override public boolean isPullToRefresh() {
    return pullToRefresh;
  }

  @Override public Exception getException() {
    return exception;
  }

  @Override public D getLoadedData() {
    return loadedData;
  }

  @Override public void setStateShowContent(D loadedData) {
    currentViewState = STATE_SHOW_CONTENT;
    this.loadedData = loadedData;
    exception = null;
  }

  @Override public void setStateShowError(Exception e, boolean pullToRefresh) {
    currentViewState = STATE_SHOW_ERROR;
    exception = e;
    this.pullToRefresh = pullToRefresh;
    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, dont clear loaded data, because of pull to refresh where previous data may
    // be displayed while showing error
  }

  @Override public void setStateShowLoading(boolean pullToRefresh) {
    currentViewState = STATE_SHOW_LOADING;
    this.pullToRefresh = pullToRefresh;
    exception = null;

    if (!pullToRefresh) {
      loadedData = null;
    }
    // else, don't clear loaded data, because of pull to refresh where previous data
    // may be displayed while showing error
  }

  @Override public boolean wasShowingError() {
    return currentViewState == STATE_SHOW_ERROR;
  }

  @Override public boolean wasShowingLoading() {
    return currentViewState == STATE_SHOW_LOADING;
  }

  @Override public boolean wasShowingContent() {
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

    // Content will be written in the subclasses
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
