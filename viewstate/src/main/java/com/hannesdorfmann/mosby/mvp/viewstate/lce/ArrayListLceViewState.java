package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;
import java.util.ArrayList;

/**
 * A {@link LceViewState} and {@link ParcelableViewState} that uses ArrayList containing
 * Parcelables as content data.
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ArrayListLceViewState<D extends ArrayList<? extends Parcelable>>
    extends AbsParcelableLceViewState<D> {

  public static final Parcelable.Creator<ArrayListLceViewState> CREATOR =
      new Parcelable.Creator<ArrayListLceViewState>() {
        @Override public ArrayListLceViewState createFromParcel(Parcel source) {
          return new ArrayListLceViewState(source);
        }

        @Override public ArrayListLceViewState[] newArray(int size) {
          return new ArrayListLceViewState[size];
        }
      };

  private static final String BUNDLE_ARRAY_LIST_WORKAROUND =
      "com.hannesdorfmann.mosby.mvp.viewstate.lce.ArrayListViewState.workaround";

  public ArrayListLceViewState() {
  }

  private ArrayListLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);

    // Content
    Bundle b = new Bundle();
    b.putParcelableArrayList(BUNDLE_ARRAY_LIST_WORKAROUND, loadedData);
    dest.writeBundle(b);
  }

  @Override
  protected void readFromParcel(Parcel source) {
    super.readFromParcel(source);

    // content
    Bundle b = source.readBundle();
    if (b != null) {
      loadedData = (D) b.getParcelableArrayList(BUNDLE_ARRAY_LIST_WORKAROUND);
    }

    // alternative ((Class) ((ParameterizedType) getClass()
    // .getGenericSuperclass()).getActualTypeArguments()[0]);
  }
}
