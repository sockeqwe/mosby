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
import java.io.Serializable;

/**
 * A {@link LceViewState} and{@link RestorableViewState} that uses a Serializeable as content data
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class SerializeableLceViewState<D extends Serializable, V extends MvpLceView<D>>
    extends AbsParcelableLceViewState<D, V> {

  public static final Parcelable.Creator<SerializeableLceViewState> CREATOR =
      new Parcelable.Creator<SerializeableLceViewState>() {
        @Override public SerializeableLceViewState createFromParcel(Parcel source) {
          return new SerializeableLceViewState(source);
        }

        @Override public SerializeableLceViewState[] newArray(int size) {
          return new SerializeableLceViewState[size];
        }
      };

  public SerializeableLceViewState() {
  }

  private SerializeableLceViewState(Parcel in) {
    readFromParcel(in);
  }

  public void writeToParcel(Parcel dest, int flags) {

    dest.writeSerializable(loadedData);
    super.writeToParcel(dest, flags);
  }

  protected void readFromParcel(Parcel in) {
    loadedData = (D) in.readSerializable();
    super.readFromParcel(in);
  }
}
