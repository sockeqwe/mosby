/*
 *  Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.mvp.delegate.mock;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * @author Hannes Dorfmann
 */
public class SimpleViewState implements RestorableViewState<SimpleView>,
    RestorableParcelableViewState<SimpleView> {

  public static final String KEY_STATE = "SimpleViewState.State";

  public static int STATE_A = 1;
  public static int STATE_B = 2;

  public int state = STATE_A;

  @Override public void saveInstanceState(@NonNull Bundle out) {
    out.putInt(KEY_STATE, state);
  }

  @Override public RestorableViewState<SimpleView> restoreInstanceState(Bundle in) {

    this.state = in.getInt(KEY_STATE);

    return this;
  }

  @Override public void apply(SimpleView view, boolean retained) {
    if (state == STATE_A) {
      view.showA();
    } else if (state == STATE_B) {
      view.showB();
    } else {
      throw new IllegalStateException("Unknown State " + state);
    }
  }

  public void setStateShowA(){
    state = STATE_A;
  }

  public void setStateShowB(){
    state = STATE_B;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(state);
  }
}


