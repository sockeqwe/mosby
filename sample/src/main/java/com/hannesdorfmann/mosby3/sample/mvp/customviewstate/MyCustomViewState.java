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

package com.hannesdorfmann.mosby3.sample.mvp.customviewstate;

import android.os.Bundle;
import android.os.Parcelable;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby3.sample.mvp.model.custom.A;
import com.hannesdorfmann.mosby3.sample.mvp.model.custom.B;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomViewState implements RestorableViewState<MyCustomView> {

  private final String KEY_STATE = "MyCustomViewState-flag";
  private final String KEY_DATA = "MyCustomViewState-data";

  public boolean showingA = true; // if false, then show B
  public Parcelable data;

  @Override public void saveInstanceState(Bundle out) {
    out.putBoolean(KEY_STATE, showingA);
    out.putParcelable(KEY_DATA, data);
  }

  @Override public RestorableViewState restoreInstanceState(Bundle in) {
    if (in == null) {
      return null;
    }

    showingA = in.getBoolean(KEY_STATE, true);
    data = in.getParcelable(KEY_DATA);
    return this;
  }

  @Override public void apply(MyCustomView view, boolean retained) {

    if (showingA) {
      view.showA((A) data);
    } else {
      view.showB((B) data);
    }
  }

  /**
   * @param a true if showing a, false if showing b
   */
  public void setShowingA(boolean a) {
    this.showingA = a;
  }

  public void setData(Parcelable data){
    this.data = data;
  }
}
