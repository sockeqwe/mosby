package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;

/**
 * A {@link LceViewState} and {@link ParcelableViewState} that uses Parcelable as content data.
 *
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @author Hannes Dorfmann
 */
public class ParcelableLceViewState<D extends Parcelable> extends AbsParcelableLceViewState<D> {

  public static final Parcelable.Creator<ParcelableLceViewState> CREATOR =
      new Parcelable.Creator<ParcelableLceViewState>() {
        @Override public ParcelableLceViewState createFromParcel(Parcel source) {
          return new ParcelableLceViewState(source);
        }

        @Override public ParcelableLceViewState[] newArray(int size) {
          return new ParcelableLceViewState[size];
        }
      };

  @Override public void saveInstanceState(Bundle out) {

  }

  private static final String BUNDLE_PARCELABLE_WORKAROUND =
      "com.hannesdorfmann.mosby.mvp.viewstate.lce.ParcelableLceViewState.workaround";

  public ParcelableLceViewState() {

  }

  private ParcelableLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);

    // content, we use a Bundle to avoid the problem of specifying a class loader.
    Bundle b = new Bundle();
    b.putParcelable(BUNDLE_PARCELABLE_WORKAROUND, loadedData);
    dest.writeBundle(b);
  }

  @Override
  protected void readFromParcel(Parcel source) {
    super.readFromParcel(source);

    Bundle b = source.readBundle();
    if (b != null) {
      loadedData = b.getParcelable(BUNDLE_PARCELABLE_WORKAROUND);
    }

    // alternative ((Class) ((ParameterizedType) getClass()
    // .getGenericSuperclass()).getActualTypeArguments()[0]);
  }
}
