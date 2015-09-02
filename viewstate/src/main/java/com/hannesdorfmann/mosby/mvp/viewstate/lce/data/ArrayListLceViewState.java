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
 * Parcelables as content data. It uses the default class loader (you could override {@link
 * #getClassLoader()}. It uses {@link Parcel#writeList(List)}
 * and {@link Parcel#readArrayList(ClassLoader)} for serialisation.
 *
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <D> the type of the data / model that is put in an ArrayList
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ArrayListLceViewState<D extends ArrayList<? extends Parcelable>, V extends MvpLceView<D>>
    extends AbsParcelableLceViewState<D, V> {

  public static final Parcelable.Creator<ArrayListLceViewState> CREATOR =
      new Parcelable.Creator<ArrayListLceViewState>() {
        @Override public ArrayListLceViewState createFromParcel(Parcel source) {
          return new ArrayListLceViewState(source);
        }

        @Override public ArrayListLceViewState[] newArray(int size) {
          return new ArrayListLceViewState[size];
        }
      };

  public ArrayListLceViewState() {
  }

  private ArrayListLceViewState(Parcel source) {
    readFromParcel(source);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {

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
