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

/**
 * A {@link LceViewState} and {@link RestorableViewState} that uses Parcelable as content data.
 * Internally it uses {@link Parcel#writeParcelable(Parcelable, int)} and {@link
 * Parcel#readParcelable(ClassLoader)} for serialisation. It uses the default class loader. You can
 * override {@link #getClassLoader()} for provide your own ClassLoader.
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

    dest.writeParcelable(loadedData, flags);
    super.writeToParcel(dest, flags);
  }

  @Override protected void readFromParcel(Parcel source) {
    loadedData = source.readParcelable(getClassLoader());
    super.readFromParcel(source);
  }

  /**
   * Get the ClassLoader that is used for deserialization
   */
  protected ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }
}
