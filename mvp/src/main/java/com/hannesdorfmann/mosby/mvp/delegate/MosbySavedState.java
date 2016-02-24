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
package com.hannesdorfmann.mosby.mvp.delegate;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * The SavedState implementation to store the view's internal id to
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
public class MosbySavedState extends View.BaseSavedState {

  public static final Parcelable.Creator<MosbySavedState> CREATOR =
      new Parcelable.Creator<MosbySavedState>() {
        public MosbySavedState createFromParcel(Parcel in) {
          return new MosbySavedState(in);
        }

        public MosbySavedState[] newArray(int size) {
          return new MosbySavedState[size];
        }
      };

  private int mosbyViewId = 0;

  public MosbySavedState(Parcelable superState) {
    super(superState);
  }

  protected MosbySavedState(Parcel in) {
    super(in);
    this.mosbyViewId = in.readInt();
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeInt(mosbyViewId);
  }

  public int getMosbyViewId() {
    return mosbyViewId;
  }

  public void setMosbyViewId(int mosbyViewId) {
    this.mosbyViewId = mosbyViewId;
  }
}
