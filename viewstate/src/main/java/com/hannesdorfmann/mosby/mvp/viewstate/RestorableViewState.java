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

package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;

/**
 * A ViewState that is parcelable. Activities can only use this kind of ViewState, because saving
 * the ViewState in a bundle as Parcelable during screen orientation changes (from portrait to
 * landscape or vice versa) is the only way to do that for activities
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface RestorableViewState<V extends MvpView> extends ViewState<V> {

  /**
   * Saves this ViewState to the outgoing bundle.
   * This will typically be called in {@link android.app.Activity#onSaveInstanceState(Bundle)}
   * or in  {@link android.app.Fragment#onSaveInstanceState(Bundle)}
   *
   * @param out The bundle where the viewstate should be stored in
   */
  public void saveInstanceState(@NonNull Bundle out);

  /**
   * Restores the viewstate that has been saved before with {@link #saveInstanceState(Bundle)}
   *
   * @param in the bundle to read the data from
   * @return null, if view state could not be restored or the restore viestate instance. Typically
   * this method will return <code>this</code>. {@link AbsParcelableLceViewState} will return a
   * copy, which is also ok.
   */
  public RestorableViewState<V> restoreInstanceState(Bundle in);
}
