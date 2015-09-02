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

import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A {@link RestorableViewState} that is implements {@link Parcelable} interface so that it can be
 * put directly in a bundle (as parcelable)
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface RestorableParcelableViewState<V extends MvpView>
    extends RestorableViewState<V>, Parcelable {
}
