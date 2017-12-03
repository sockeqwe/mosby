/*
 * Copyright 2017 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.mvi;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * This type of presenter is responsible for interaction with the viewState in a Model-View-Intent way.
 * It is the bridge that is responsible for setting up the reactive flow between viewState and model.
 *
 * @param <V> The type of the View this presenter responds to
 * @param <VS> The type of the ViewState (Model)
 * @author Hannes Dorfmann
 * @since 3.0
 */
public interface MviPresenter<V extends MvpView, VS> extends MvpPresenter<V> {
}
