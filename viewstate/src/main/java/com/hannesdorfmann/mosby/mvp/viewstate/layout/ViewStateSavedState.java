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
package com.hannesdorfmann.mosby.mvp.viewstate.layout;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;

/**
 * The SavedState implementation to store the view state in layouts
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
public class ViewStateSavedState extends View.BaseSavedState {

  public static final Parcelable.Creator<ViewStateSavedState> CREATOR =
      new Parcelable.Creator<ViewStateSavedState>() {
        public ViewStateSavedState createFromParcel(Parcel in) {
          return new ViewStateSavedState(in);
        }

        public ViewStateSavedState[] newArray(int size) {
          return new ViewStateSavedState[size];
        }
      };

  private RestorableParcelableViewState mosbyViewState;

  public ViewStateSavedState(Parcelable superState) {
    super(superState);
  }

  private ViewStateSavedState(Parcel in) {
    super(in);
    this.mosbyViewState = in.readParcelable(RestorableParcelableViewState.class.getClassLoader());
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeParcelable(mosbyViewState, flags);
  }

  public RestorableParcelableViewState getMosbyViewState() {
    return mosbyViewState;
  }

  public void setMosbyViewState(RestorableParcelableViewState mosbyViewState) {
    this.mosbyViewState = mosbyViewState;
  }
}
