/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.hannesdorfmann.mosby3.mvi.rx;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PublishProxySubjectTest {

  @Test public void onNextAfterOnCompleted() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    subject.subscribe(observer);

    subject.onNext("one");
    verify(observer).onNext("one");
    subject.onNext("two");
    verify(observer).onNext("two");
    subject.onNext("three");
    verify(observer).onNext("three");
    subject.onComplete();
    verify(observer).onComplete();

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onNext("four");
    verify(observer).onNext("four");
    verify(anotherSubscriber).onNext("four");

    subject.onComplete();
    verify(observer, times(2)).onComplete();
    verify(anotherSubscriber).onComplete();

    subject.onError(testException);
    verify(observer).onError(testException);
    verify(anotherSubscriber).onError(testException);
  }

  @Test public void onNextAfterOnError() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    subject.subscribe(observer);

    subject.onNext("one");
    verify(observer).onNext("one");
    subject.onNext("two");
    verify(observer).onNext("two");
    subject.onNext("three");
    verify(observer).onNext("three");
    subject.onError(testException);
    verify(observer).onError(testException);

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onNext("four");
    verify(observer).onNext("four");
    verify(anotherSubscriber).onNext("four");

    subject.onError(testException);
    verify(observer, times(2)).onError(testException);
    verify(anotherSubscriber).onError(testException);
  }

  @Test public void onCompleteAfterOnError() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    subject.subscribe(observer);

    subject.onNext("one");
    verify(observer).onNext("one");
    subject.onNext("two");
    verify(observer).onNext("two");
    subject.onNext("three");
    verify(observer).onNext("three");
    subject.onError(testException);
    verify(observer).onError(testException);

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onComplete();
    verify(observer).onComplete();
    verify(anotherSubscriber).onComplete();
  }

  @Test public void onErrorAfterOnComplete() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    subject.subscribe(observer);

    subject.onNext("one");
    verify(observer).onNext("one");
    subject.onNext("two");
    verify(observer).onNext("two");
    subject.onNext("three");
    verify(observer).onNext("three");
    subject.onComplete();
    verify(observer).onComplete();

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onError(testException);
    verify(observer).onError(testException);
    verify(anotherSubscriber).onError(testException);
  }

  private void assertCompletedSubscriber(Observer<String> observer) {
    verify(observer, times(1)).onNext("one");
    verify(observer, times(1)).onNext("two");
    verify(observer, times(1)).onNext("three");
    verify(observer, Mockito.never()).onError(any(Throwable.class));
    verify(observer, times(1)).onComplete();
  }


  private void assertErrorSubscriber(Observer<String> observer) {
    verify(observer, times(1)).onNext("one");
    verify(observer, times(1)).onNext("two");
    verify(observer, times(1)).onNext("three");
    verify(observer, times(1)).onError(testException);
    verify(observer, Mockito.never()).onComplete();
  }

  @Test public void testSubscribeMidSequence() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    subject.subscribe(observer);

    subject.onNext("one");
    subject.onNext("two");

    assertObservedUntilTwo(observer);

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onNext("three");
    subject.onComplete();

    assertCompletedSubscriber(observer);
    assertCompletedStartingWithThreeSubscriber(anotherSubscriber);
  }

  private void assertCompletedStartingWithThreeSubscriber(Observer<String> observer) {
    verify(observer, Mockito.never()).onNext("one");
    verify(observer, Mockito.never()).onNext("two");
    verify(observer, times(1)).onNext("three");
    verify(observer, Mockito.never()).onError(any(Throwable.class));
    verify(observer, times(1)).onComplete();
  }

  @Test public void testUnsubscribeFirstSubscriber() {
    PublishProxySubject<String> subject = PublishProxySubject.create();

    Observer<String> observer = TestHelper.mockObserver();
    TestObserver<String> ts = new TestObserver<String>(observer);
    subject.subscribe(ts);

    subject.onNext("one");
    subject.onNext("two");

    ts.dispose();
    assertObservedUntilTwo(observer);

    Observer<String> anotherSubscriber = TestHelper.mockObserver();
    subject.subscribe(anotherSubscriber);

    subject.onNext("three");
    subject.onComplete();

    assertObservedUntilTwo(observer);
    assertCompletedStartingWithThreeSubscriber(anotherSubscriber);
  }

  private void assertObservedUntilTwo(Observer<String> observer) {
    verify(observer, times(1)).onNext("one");
    verify(observer, times(1)).onNext("two");
    verify(observer, Mockito.never()).onNext("three");
    verify(observer, Mockito.never()).onError(any(Throwable.class));
    verify(observer, Mockito.never()).onComplete();
  }

  @Test public void testNestedSubscribe() {
    final PublishProxySubject<Integer> s = PublishProxySubject.create();

    final AtomicInteger countParent = new AtomicInteger();
    final AtomicInteger countChildren = new AtomicInteger();
    final AtomicInteger countTotal = new AtomicInteger();

    final ArrayList<String> list = new ArrayList<String>();

    s.flatMap(new Function<Integer, Observable<String>>() {

      @Override public Observable<String> apply(final Integer v) {
        countParent.incrementAndGet();

        // then subscribe to subject again (it will not receive the previous value)
        return s.map(new Function<Integer, String>() {

          @Override public String apply(Integer v2) {
            countChildren.incrementAndGet();
            return "Parent: " + v + " Child: " + v2;
          }
        });
      }
    }).subscribe(new Consumer<String>() {

      @Override public void accept(String v) {
        countTotal.incrementAndGet();
        list.add(v);
      }
    });

    for (int i = 0; i < 10; i++) {
      s.onNext(i);
    }
    s.onComplete();

    //            System.out.println("countParent: " + countParent.get());
    //            System.out.println("countChildren: " + countChildren.get());
    //            System.out.println("countTotal: " + countTotal.get());

    // 9+8+7+6+5+4+3+2+1+0 == 45
    assertEquals(45, list.size());
  }

  /**
   * Should be able to unsubscribe all Subscribers, have it stop emitting, then subscribe new ones and it start emitting again.
   */
  @Test public void testReSubscribe() {
    final PublishProxySubject<Integer> ps = PublishProxySubject.create();

    Observer<Integer> o1 = TestHelper.mockObserver();
    TestObserver<Integer> ts = new TestObserver<Integer>(o1);
    ps.subscribe(ts);

    // emit
    ps.onNext(1);

    // validate we got it
    InOrder inOrder1 = inOrder(o1);
    inOrder1.verify(o1, times(1)).onNext(1);
    inOrder1.verifyNoMoreInteractions();

    // unsubscribe
    ts.dispose();

    // emit again but nothing will be there to receive it
    ps.onNext(2);

    Observer<Integer> o2 = TestHelper.mockObserver();
    TestObserver<Integer> ts2 = new TestObserver<Integer>(o2);
    ps.subscribe(ts2);

    // emit
    ps.onNext(3);

    // validate we got it
    InOrder inOrder2 = inOrder(o2);
    inOrder2.verify(o2, times(1)).onNext(3);
    inOrder2.verifyNoMoreInteractions();

    ts2.dispose();
  }

  private final Throwable testException = new Throwable();

  @Test(timeout = 1000) public void testUnsubscriptionCase() {
    PublishProxySubject<String> src = PublishProxySubject.create();

    for (int i = 0; i < 10; i++) {
      final Observer<Object> o = TestHelper.mockObserver();
      InOrder inOrder = inOrder(o);
      String v = "" + i;
      System.out.printf("Turn: %d%n", i);
      src.firstElement().toObservable().flatMap(new Function<String, Observable<String>>() {

        @Override public Observable<String> apply(String t1) {
          return Observable.just(t1 + ", " + t1);
        }
      }).subscribe(new DefaultObserver<String>() {
        @Override public void onNext(String t) {
          o.onNext(t);
        }

        @Override public void onError(Throwable e) {
          o.onError(e);
        }

        @Override public void onComplete() {
          o.onComplete();
        }
      });
      src.onNext(v);

      inOrder.verify(o).onNext(v + ", " + v);
      inOrder.verify(o).onComplete();
      verify(o, never()).onError(any(Throwable.class));
    }
  }

  @Test public void testCurrentStateMethodsNormal() {
    PublishProxySubject<Object> as = PublishProxySubject.create();

    assertFalse(as.hasThrowable());
    assertFalse(as.hasComplete());
    assertNull(as.getThrowable());

    as.onNext(1);

    assertFalse(as.hasThrowable());
    assertFalse(as.hasComplete());
    assertNull(as.getThrowable());

    as.onComplete();

    assertFalse(as.hasThrowable());
    assertTrue(as.hasComplete());
    assertNull(as.getThrowable());
  }

  @Test public void testCurrentStateMethodsEmpty() {
    PublishProxySubject<Object> as = PublishProxySubject.create();

    assertFalse(as.hasThrowable());
    assertFalse(as.hasComplete());
    assertNull(as.getThrowable());

    as.onComplete();

    assertFalse(as.hasThrowable());
    assertTrue(as.hasComplete());
    assertNull(as.getThrowable());
  }

  @Test public void testCurrentStateMethodsError() {
    PublishProxySubject<Object> as = PublishProxySubject.create();

    assertFalse(as.hasThrowable());
    assertFalse(as.hasComplete());
    assertNull(as.getThrowable());

    as.onError(new TestException());

    assertTrue(as.hasThrowable());
    assertFalse(as.hasComplete());
    assertTrue(as.getThrowable() instanceof TestException);
  }

  @Test public void crossCancel() {
    final TestObserver<Integer> ts1 = new TestObserver<Integer>();
    TestObserver<Integer> ts2 = new TestObserver<Integer>() {
      @Override public void onNext(Integer t) {
        super.onNext(t);
        ts1.cancel();
      }
    };

    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    pp.subscribe(ts2);
    pp.subscribe(ts1);

    pp.onNext(1);

    ts2.assertValue(1);

    ts1.assertNoValues();
  }

  @Test public void crossCancelOnError() {
    final TestObserver<Integer> ts1 = new TestObserver<Integer>();
    TestObserver<Integer> ts2 = new TestObserver<Integer>() {
      @Override public void onError(Throwable t) {
        super.onError(t);
        ts1.cancel();
      }
    };

    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    pp.subscribe(ts2);
    pp.subscribe(ts1);

    pp.onError(new TestException());

    ts2.assertError(TestException.class);

    ts1.assertNoErrors();
  }

  @Test public void crossCancelOnComplete() {
    final TestObserver<Integer> ts1 = new TestObserver<Integer>();
    TestObserver<Integer> ts2 = new TestObserver<Integer>() {
      @Override public void onComplete() {
        super.onComplete();
        ts1.cancel();
      }
    };

    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    pp.subscribe(ts2);
    pp.subscribe(ts1);

    pp.onComplete();

    ts2.assertComplete();

    ts1.assertNotComplete();
  }

  @Test public void onSubscribeCancelsImmediately() {
    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    TestObserver<Integer> ts = pp.test();

    pp.subscribe(new Observer<Integer>() {

      @Override public void onSubscribe(Disposable s) {
        s.dispose();
        s.dispose();
      }

      @Override public void onNext(Integer t) {

      }

      @Override public void onError(Throwable t) {

      }

      @Override public void onComplete() {

      }
    });

    ts.cancel();

    assertFalse(pp.hasObservers());
  }

  @Test public void nullOnNext() {
    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    TestObserver<Integer> ts = pp.test();

    assertTrue(pp.hasObservers());

    pp.onNext(null);

    ts.assertFailure(NullPointerException.class);
  }

  @Test public void nullOnError() {
    PublishProxySubject<Integer> pp = PublishProxySubject.create();

    TestObserver<Integer> ts = pp.test();

    pp.onError(null);

    ts.assertFailure(NullPointerException.class);
  }

  @Test public void subscribedTo() {
    PublishProxySubject<Integer> pp = PublishProxySubject.create();
    PublishProxySubject<Integer> pp2 = PublishProxySubject.create();

    pp.subscribe(pp2);

    TestObserver<Integer> ts = pp2.test();

    pp.onNext(1);
    pp.onNext(2);
    pp.onComplete();

    ts.assertResult(1, 2);
  }

  @Test public void onNextNull() {
    final PublishProxySubject<Object> s = PublishProxySubject.create();

    s.onNext(null);

    s.test()
        .assertNoValues()
        .assertError(NullPointerException.class)
        .assertErrorMessage(
            "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
  }

  @Test public void onErrorNull() {
    final PublishProxySubject<Object> s = PublishProxySubject.create();

    s.onError(null);

    s.test()
        .assertNoValues()
        .assertError(NullPointerException.class)
        .assertErrorMessage(
            "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
  }
}