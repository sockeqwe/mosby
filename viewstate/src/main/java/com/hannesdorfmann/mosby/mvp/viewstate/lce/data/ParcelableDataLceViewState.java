package com.hannesdorfmann.mosby.mvp.viewstate.lce.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * A {@link LceViewState} and {@link ParcelableViewState} that uses Parcelable as content data.
 *
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ParcelableDataLceViewState<D extends Parcelable, V extends MvpLceView<D>>
    extends AbsParcelableLceViewState<D, V> {

  public static final Parcelable.Creator<ParcelableDataLceViewState> CREATOR =
      new Parcelable.Creator<ParcelableDataLceViewState>() {
        @Override public ParcelableDataLceViewState createFromParcel(Parcel source) {
          return new ParcelableDataLceViewState(source);
        }

        @Override public ParcelableDataLceViewState[] newArray(int size) {
          return new ParcelableDataLceViewState[size];
        }
      };


  private static final String BUNDLE_PARCELABLE_WORKAROUND =
      "com.hannesdorfmann.mosby.mvp.viewstate.lce.ParcelableLceViewState.workaround";

  public ParcelableDataLceViewState() {
  }

  private ParcelableDataLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);

    // content, we use a Bundle to avoid the problem of specifying a class loader.
    Bundle b = new Bundle();
    b.putParcelable(BUNDLE_PARCELABLE_WORKAROUND, loadedData);
    dest.writeBundle(b);
  }

  @Override protected void readFromParcel(Parcel source) {
    super.readFromParcel(source);

    Bundle b = source.readBundle();
    if (b != null) {
      loadedData = b.getParcelable(BUNDLE_PARCELABLE_WORKAROUND);
    }

    // alternative ((Class) ((ParameterizedType) getClass()
    // .getGenericSuperclass()).getActualTypeArguments()[0]);
  }
}
