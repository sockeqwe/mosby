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

package com.hannesdorfmann.mosby3.mvp.viewstate.lce.data;

import android.support.v4.app.Fragment;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;

/**
 * This kind of {@link LceViewState} can be used with <b>Activities and Fragments that have set
 * setRetainInstance(true); <b/>. This allows to store / restore any kind of data along
 * orientation changes. So this ViewState will not be saved into the Bundle of saveInstanceState().
 * This ViewState will be stored and restored directly by the Activity or Fragment itself.
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 * @deprecated Use {@link RetainingLceViewState} instead.
 */
@Deprecated public class RetainingFragmentLceViewState<D, V extends MvpLceView<D>>
    extends RetainingLceViewState<D, V> {

  /**
   * Creates a new instance. Since most of developers forget to call {@link
   * Fragment#setRetainInstance(boolean)} you have to pass the fragment and
   * setRetainInstance(true) will be called for that fragment
   *
   * @param f The fragment for this view state. Can be null if you really don't want to
   * setRetainInstanceState(true) automatically, but you should really have a good reason for doing
   * so
   */
  public RetainingFragmentLceViewState(Fragment f) {
    if (f != null) {
      f.setRetainInstance(true);
    }
  }
}
