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

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.ActivityMvpViewStateDelegateCallback;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * A class for partial mocking
 * @author Hannes Dorfmann
 */
public abstract class PartialActivityViewStateCallbackImpl
    implements ActivityMvpViewStateDelegateCallback<SimpleView, MvpPresenter<SimpleView>> {

  private ViewState<SimpleView> viewState;
  private MvpPresenter<SimpleView> presenter;

  @Override public void setViewState(ViewState<SimpleView> viewState) {
    this.viewState = viewState;
  }

  @Override public ViewState<SimpleView> getViewState() {
    return viewState;
  }

  @Override public void setPresenter(MvpPresenter<SimpleView> presenter) {
    this.presenter = presenter;
  }

  @Override public MvpPresenter<SimpleView> getPresenter() {
    return presenter;
  }
}
