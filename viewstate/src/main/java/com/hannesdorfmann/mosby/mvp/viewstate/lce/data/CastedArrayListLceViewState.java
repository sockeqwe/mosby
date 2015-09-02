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

package com.hannesdorfmann.mosby.mvp.viewstate.lce.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LceViewState} and {@link RestorableViewState} that uses ArrayList containing
 * Parcelables as content data. This class takes a <code>List<? extends Parcelable></code> but
 * assumes that it is an instance of ArrayList. It uses {@link Parcel#writeList(List)}
 * and {@link Parcel#readArrayList(ClassLoader)} for serialisation
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <D> the type of the data / model that is put in an ArrayList
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class CastedArrayListLceViewState<D extends List<? extends Parcelable>, V extends MvpLceView<D>>
    extends AbsParcelableLceViewState<D, V> {

  public static final Creator<CastedArrayListLceViewState> CREATOR =
      new Creator<CastedArrayListLceViewState>() {
        @Override public CastedArrayListLceViewState createFromParcel(Parcel source) {
          return new CastedArrayListLceViewState(source);
        }

        @Override public CastedArrayListLceViewState[] newArray(int size) {
          return new CastedArrayListLceViewState[size];
        }
      };

  public CastedArrayListLceViewState() {
  }

  protected CastedArrayListLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {

    if (loadedData != null && !(loadedData instanceof ArrayList)) {
      throw new ClassCastException(
          "You try to use CastedArrayListLceViewState which takes List<D> as argument but "
              + "assumes that it's an instance of ArrayList<D>. Howerver, your loaded data is not an ArrayList"
              + " but it's of type "
              + loadedData.getClass().getCanonicalName()
              + " which is not supported");
    }

    // Content
    dest.writeList(loadedData);

    super.writeToParcel(dest, flags);
  }

  @Override protected void readFromParcel(Parcel source) {

    loadedData = (D) source.readArrayList(getClassLoader());
    super.readFromParcel(source);
  }

  /**
   * The class loader used for deserializing the list of parcelable items
   */
  protected ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }
}
