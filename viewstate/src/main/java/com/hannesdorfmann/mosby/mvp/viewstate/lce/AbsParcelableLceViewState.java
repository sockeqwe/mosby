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

package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import android.os.Bundle;
import android.os.Parcel;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * Extends {@link AbsLceViewState} by implementing {@link
 * RestorableViewState}. This class can be saved and restored in a bundle. Therefore it can be
 * used
 * for Activities and Fragments.
 *
 * <p>Please note, that {@link #restoreInstanceState(Bundle)}</p> will create a new copy of this
 * view state and return that one instead of the current object instance. That's fine and makes
 * working custom view states much easier. This note is just to inform you that the view state
 * refrence attached to the view may change during restroing view state.
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class AbsParcelableLceViewState<D, V extends MvpLceView<D>>
    extends AbsLceViewState<D, V> implements ParcelableLceViewState<D, V> {

  public static final String KEY_BUNDLE_VIEW_STATE =
      "com.hannesdorfmann.mosby.mvp.viewstate.ViewState.bundlekey";

  @Override public void saveInstanceState(Bundle out) {
    out.putParcelable(KEY_BUNDLE_VIEW_STATE, this);
  }

  @Override public AbsParcelableLceViewState<D, V> restoreInstanceState(Bundle in) {
    if (in == null) {
      return null;
    }

    // Workaround to solve class loader problem.
    // But it returns a copy of the view state and not this viewstate. However, that's ok!
    return (AbsParcelableLceViewState<D, V>) in.getParcelable(KEY_BUNDLE_VIEW_STATE);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {

    dest.writeInt(currentViewState);

    // PullToRefresh
    writeBoolean(dest, pullToRefresh);

    // write exception
    dest.writeSerializable(exception);

    // Content will be written in the subclasses
  }

  protected void readFromParcel(Parcel in) {
    currentViewState = in.readInt();

    // Pull To Refresh
    pullToRefresh = readBoolean(in);

    exception = (Throwable) in.readSerializable();

    // content will be read in subclass
  }

  protected void writeBoolean(Parcel dest, boolean b) {
    dest.writeByte((byte) (b ? 1 : 0));
  }

  protected boolean readBoolean(Parcel p) {
    return p.readByte() == (byte) 1;
  }
}
