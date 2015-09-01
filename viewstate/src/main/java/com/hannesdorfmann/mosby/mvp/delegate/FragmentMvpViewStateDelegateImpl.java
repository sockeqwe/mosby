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

package com.hannesdorfmann.mosby.mvp.delegate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * The {@link FragmentMvpDelegateImpl} with {@link ViewState} support
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public class FragmentMvpViewStateDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    extends FragmentMvpDelegateImpl<V, P> {

  public FragmentMvpViewStateDelegateImpl(BaseMvpViewStateDelegateCallback<V, P> delegateCallback) {
    super(delegateCallback);
  }

  @Override protected MvpInternalDelegate<V, P> getInternalDelegate() {
    if (internalDelegate == null) {
      internalDelegate =
          new MvpViewStateInternalDelegate<V, P>((BaseMvpViewStateDelegateCallback) delegateCallback);
    }

    return internalDelegate;
  }

  @Override public void onCreate(Bundle saved) {
    super.onCreate(saved);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).createOrRestoreViewState(saved);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).applyViewState();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).saveViewState(outState);
  }
}
