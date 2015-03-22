package com.hannesdorfmann.mosby.mvp.viewstate.lce.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import java.util.ArrayList;

/**
 * A {@link LceViewState} and {@link RestoreableViewState} that uses ArrayList containing
 * Parcelables as content data.
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <D> the type of the data / model that is put in an ArrayList
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ArrayListLceViewState<D extends Parcelable, V extends MvpLceView<ArrayList<D>>>
    extends AbsParcelableLceViewState<ArrayList<D>, V> {

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

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);

    // Content
    Bundle b = new Bundle();
    b.putParcelableArrayList(BUNDLE_ARRAY_LIST_WORKAROUND, loadedData);
    dest.writeBundle(b);
  }

  @Override protected void readFromParcel(Parcel source) {
    super.readFromParcel(source);

    // content
    Bundle b = source.readBundle();
    if (b != null) {
      loadedData = (ArrayList<D>) b.getParcelableArrayList(BUNDLE_ARRAY_LIST_WORKAROUND);
    }

    // alternative ((Class) ((ParameterizedType) getClass()
    // .getGenericSuperclass()).getActualTypeArguments()[0]);
  }
}
