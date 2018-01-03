/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.mvp.viewstate.lce.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LceViewState} and {@link RestorableViewState} that uses a <code>List<? extends
 * Parcelable></code> containing Parcelables as content data.
 *
 * @param <D> the type of the data / model
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public class ParcelableListLceViewState<D extends List<? extends Parcelable>, V extends MvpLceView<D>>
    extends AbsParcelableLceViewState<D, V> {

  public static final Creator<ParcelableListLceViewState> CREATOR =
      new Creator<ParcelableListLceViewState>() {
        @Override public ParcelableListLceViewState createFromParcel(Parcel source) {
          return new ParcelableListLceViewState(source);
        }

        @Override public ParcelableListLceViewState[] newArray(int size) {
          return new ParcelableListLceViewState[size];
        }
      };

  public ParcelableListLceViewState() {
  }

  protected ParcelableListLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
/*
    if (loadedData != null && !(loadedData instanceof ArrayList)) {
      throw new ClassCastException(
          "You try to use CastedArrayListLceViewState which takes List<D> as argument but "
              + "assumes that it's an instance of ArrayList<D>. Howerver, your loaded data is not an ArrayList"
              + " but it's of type "
              + loadedData.getClass().getCanonicalName()
              + " which is not supported");
    }

    */

    // Content
    dest.writeList(loadedData);

    super.writeToParcel(dest, flags);
  }

  @Override protected void readFromParcel(Parcel source) {
    loadedData = (D) new ArrayList<Parcelable>();
    source.readList(loadedData, getClassLoader());
    super.readFromParcel(source);
  }

  /**
   * The class loader used for deserializing the list of parcelable items
   */
  protected ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }
}
