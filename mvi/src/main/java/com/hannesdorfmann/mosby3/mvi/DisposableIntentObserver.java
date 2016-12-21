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

package com.hannesdorfmann.mosby3.mvi;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Just a simple {@link DisposableObserver} that is used to cancel subscriptions from view's
 * intent to the internal relays
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
class DisposableIntentObserver<I> extends DisposableObserver<I> {

  private final PublishSubject<I> subject;

  public DisposableIntentObserver(PublishSubject<I> subject) {
    this.subject = subject;
  }

  @Override public void onNext(I value) {
    subject.onNext(value);
  }

  @Override public void onError(Throwable e) {
    throw new IllegalStateException("View intents must not throw errors.", e);
  }

  @Override public void onComplete() {
    subject.onComplete();
  }
}
