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
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * If you really have good reasons you could have <i>Void</i> as content type in a LCE
 * (Loading-Content-Error) View. This is the corresponding {@link LceViewState} and{@link
 * RestorableViewState}
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <V> The type of the view
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class VoidViewState<V extends MvpLceView<Void>> extends AbsParcelableLceViewState<Void, V> {
}
