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

package com.hannesdorfmann.mosby.mvp.viewstate.lce.data;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * This kind of {@link LceViewState} can be used with <b>Activities and Fragments that have set
 * setRetainInstance(true); <b/>. This allows to store / restore any kind of data along
 * orientation changes. So this ViewState will not be saved into the Bundle of saveInstanceState().
 * This ViewState will be stored and restored directly by the Activity or Fragment itself.
 *
 * @param <D> the data / model type
 * @param <V> the type of the view
 * @author Hannes Dorfmann
 * @since 2.0.0
 */
public class RetainingLceViewState<D, V extends MvpLceView<D>> extends AbsLceViewState<D, V> {

}
