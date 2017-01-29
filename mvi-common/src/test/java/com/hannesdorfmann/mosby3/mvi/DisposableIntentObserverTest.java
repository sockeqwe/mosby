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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED"})
public class DisposableIntentObserverTest {

  @Test public void forwardOnNextAndOnCompleteToPublishShubject() {
    PublishSubject<String> subject = PublishSubject.create();

    TestObserver<String> sub = new TestObserver<>();
    subject.subscribeWith(sub);

    Observable.just("Hello").subscribe(new DisposableIntentObserver<String>(subject));

    sub.assertNoErrors().assertComplete().assertResult("Hello");
  }

  @Test public void error() {
    PublishSubject subject = PublishSubject.create();
    TestObserver sub = new TestObserver<>();
    subject.subscribeWith(sub);

    Exception originalException = new RuntimeException("I am the original Exception");
    Exception expectedException =
        new IllegalStateException("View intents must not throw errors", originalException);
    try {

      Observable.error(originalException).subscribe(new DisposableIntentObserver(subject));

      Assert.fail("Exception expected");
    } catch (Exception e) {
      Throwable cause = e.getCause();
      Assert.assertEquals(expectedException.getMessage(), cause.getMessage());
      Assert.assertEquals(originalException, cause.getCause());
    }

    sub.assertNotComplete().assertNoValues();
  }
}
