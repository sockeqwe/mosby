/*
 * Copyright 2016 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle;

import android.util.Log;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;

/**
 * @author Hannes Dorfmann
 */

public class LifecycleTestPresenter extends MviBasePresenter<LifecycleTestView, Object> {

  public int attachViewInvokations = 0;
  public LifecycleTestView attachedView;
  public int detachViewInvokations = 0;
  public boolean onDettachViewRetainInstance = false;
  public int bindIntentInvocations = 0;

  @Override public void attachView(LifecycleTestView view) {
    super.attachView(view);
    attachViewInvokations++;
    attachedView = view;
    Log.d(getClass().getSimpleName(), "attachView " + attachViewInvokations + " " + attachedView);
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    attachedView = null;
    detachViewInvokations++;
    onDettachViewRetainInstance = retainInstance;
    Log.d(getClass().getSimpleName(), "detachView " + detachViewInvokations + " " + retainInstance);
  }

  @Override protected void bindIntents() {
    if (bindIntentInvocations >= 1) {
      throw new IllegalStateException(
          "bindIntents() is called more than once. Invokations: " + bindIntentInvocations);
    }
    bindIntentInvocations++;
  }
}
