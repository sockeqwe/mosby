package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LceViewState} and {@link ParcelableViewState} that uses ArrayList containing
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
public class CastedArrayListLceViewState<D extends Parcelable, V extends MvpLceView<List<D>>>
    extends AbsParcelableLceViewState<List<D>, V> {

  public static final Creator<CastedArrayListLceViewState> CREATOR =
      new Creator<CastedArrayListLceViewState>() {
        @Override public CastedArrayListLceViewState createFromParcel(Parcel source) {
          return new CastedArrayListLceViewState(source);
        }

        @Override public CastedArrayListLceViewState[] newArray(int size) {
          return new CastedArrayListLceViewState[size];
        }
      };

  private static final String BUNDLE_ARRAY_LIST_WORKAROUND =
      "com.hannesdorfmann.mosby.mvp.viewstate.lce.CastedArrayListViewState.workaround";

  public CastedArrayListLceViewState() {
  }

  private CastedArrayListLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);

    if (loadedData != null && !(loadedData instanceof ArrayList)) {
      throw new ClassCastException(
          "You try to use CastedArrayListLceViewState which takes List<D> as argument but "
              + "assumes that it's an ArrayList<D>. Howerver, your loaded data if not an ArrayList"
              + " but it's of type "
              + loadedData.getClass().getCanonicalName()
              + " which is not supported");
    }

    // Content
    Bundle b = new Bundle();
    b.putParcelableArrayList(BUNDLE_ARRAY_LIST_WORKAROUND, (ArrayList<D>) loadedData);
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
