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

import android.support.v4.app.Fragment;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A ViewState is, like the name suggests, responsible to store the views state. In other words:
 * The
 * view like an Activity or a Fragment stores his state, like "showing loading animation", showing
 * error view, etc. The goal is to have a views that can easily restore there state after screen
 * orientation changes (from portrait to landscape and vice versa) by using a ViewState and the
 * well
 * defined View interfaces. The idea is to call the same methods the presenter would call to
 * restore
 * the view's state.
 * <p>
 * While fragments can restore every data object if you use {@link Fragment#setRetainInstance(boolean)}
 * = true Activities can't do that.
 * Therefore Activities have to use {@link RestorableViewState}.
 * </p>
 *
 * @param <V> The type of the View (extends {@link MvpView}
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface ViewState<V extends MvpView> {

  /**
   * Called to apply this viewstate on a given view.
   *
   * @param view The {@link MvpView}
   * @param retained true, if the components like the viewstate and the presenter have been
   * retained
   * because the {@link Fragment#setRetainInstance(boolean)} has been set to true
   */
  public void apply(V view, boolean retained);
}
