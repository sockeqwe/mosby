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
package com.hannesdorfmann.mosby3.mvp.delegate;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import com.hannesdorfmann.mosby3.MosbySavedState;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableParcelableViewState;

/**
 * The SavedState implementation to store the {@link RestorableParcelableViewState} plus mosby view
 * id
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
public class MosbyViewStateSavedState extends MosbySavedState {

  public static final Parcelable.Creator<MosbyViewStateSavedState> CREATOR =
      ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<MosbyViewStateSavedState>() {
        public MosbyViewStateSavedState createFromParcel(Parcel in, ClassLoader loader) {
          if (loader == null) {
            loader = RestorableParcelableViewState.class.getClassLoader();
          }
          return new MosbyViewStateSavedState(in, loader);
        }

        public MosbyViewStateSavedState[] newArray(int size) {
          return new MosbyViewStateSavedState[size];
        }
      });

  private RestorableParcelableViewState mosbyViewState;

  public MosbyViewStateSavedState(Parcelable superState, @NonNull String viewId,
      @Nullable RestorableParcelableViewState viewState) {
    super(superState, viewId);
    this.mosbyViewState = viewState;
  }

  protected MosbyViewStateSavedState(Parcel in, ClassLoader loader) {
    super(in, loader);
    this.mosbyViewState = in.readParcelable(loader);
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeParcelable(mosbyViewState, flags);
  }

  @Nullable
  public RestorableParcelableViewState getRestoreableViewState() {
    return mosbyViewState;
  }
}
