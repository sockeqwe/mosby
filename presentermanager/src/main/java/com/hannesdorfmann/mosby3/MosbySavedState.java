/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.hannesdorfmann.mosby3;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.os.ParcelableCompat;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import androidx.customview.view.AbsSavedState;

/**
 * The SavedState implementation to store the view's internal id to
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
public class MosbySavedState extends AbsSavedState {

  public static final Creator<MosbySavedState> CREATOR =
      ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<MosbySavedState>() {
        public MosbySavedState createFromParcel(Parcel in, ClassLoader loader) {
          if (loader == null) {
            loader = MosbySavedState.class.getClassLoader();
          }
          return new MosbySavedState(in, loader);
        }

        public MosbySavedState[] newArray(int size) {
          return new MosbySavedState[size];
        }
      });

  private String mosbyViewId;

  public MosbySavedState(Parcelable superState, String mosbyViewId) {
    super(superState);
    this.mosbyViewId = mosbyViewId;
  }

  protected MosbySavedState(Parcel in, ClassLoader loader) {
    super(in, loader);
    this.mosbyViewId = in.readString();
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeString(mosbyViewId);
  }

  public String getMosbyViewId() {
    return mosbyViewId;
  }

}
