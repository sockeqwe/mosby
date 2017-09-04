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

package com.hannesdorfmann.mosby3.mvi.integrationtest.backstack.second;

import android.support.annotation.NonNull;
import android.util.Log;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hannes Dorfmann
 */

public class SecondPresenter extends MviBasePresenter<SecondView, Object> {

  public AtomicInteger bindIntentCalls = new AtomicInteger(0);
  public AtomicInteger unbindIntentCalls = new AtomicInteger(0);
  public AtomicInteger attachViewCalls = new AtomicInteger(0);
  public AtomicInteger detachViewCalls = new AtomicInteger(0);
  public AtomicInteger destroyCalls = new AtomicInteger(0);


  @Override protected void bindIntents() {
    bindIntentCalls.incrementAndGet();
  }

  @Override protected void unbindIntents() {
    super.unbindIntents();
    unbindIntentCalls.incrementAndGet();
  }

  @Override public void attachView(@NonNull SecondView view) {
    super.attachView(view);
    attachViewCalls.incrementAndGet();
  }

  @Override public void detachView() {
    super.detachView();
    Log.d("Presenters", "SecondPresenter detachView Presenter");
    detachViewCalls.incrementAndGet();
  }

  @Override public void destroy() {
    super.destroy();
    destroyCalls.incrementAndGet();
  }
}
