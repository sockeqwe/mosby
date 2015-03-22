package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;

/**
 * Extends {@link AbsLceViewState} by implementing {@link
 * RestoreableViewState}. This class can be saved and restored in a bundle. Therefore it can be used
 * for Activities and Fragments.
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class AbsParcelableLceViewState<D, V extends MvpLceView<D>>
    extends AbsLceViewState<D, V> implements ParcelableLceViewState<D, V> {

  public static final String KEY_BUNDLE_VIEW_STATE =
      "com.hannesdorfmann.mosby.mvp.viewstate.ViewState.bundlekey";

  @Override public void saveInstanceState(Bundle out) {
    out.putParcelable(KEY_BUNDLE_VIEW_STATE, this);
  }

  @Override public boolean restoreInstanceState(Bundle in) {
    if (in == null) {
      return false;
    }

    // Workaround
    AbsParcelableLceViewState<D, V> tmp =
        (AbsParcelableLceViewState<D, V>) in.getParcelable(KEY_BUNDLE_VIEW_STATE);
    this.loadedData = tmp.loadedData;
    this.currentViewState = tmp.currentViewState;
    this.exception = tmp.exception;
    this.pullToRefresh = tmp.pullToRefresh;
    return true;
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
