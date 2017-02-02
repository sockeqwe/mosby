package com.hannesdorfmann.mosby3.mvi;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Just a simple {@link DisposableObserver} that is used to cancel subscriptions from view's
 * state to the internal relays
 */
class DisposableViewStateObserver<VS> extends DisposableObserver<VS> {
  private final BehaviorSubject<VS> subject;

  public DisposableViewStateObserver(BehaviorSubject<VS> subject) {
        this.subject = subject;
    }

  @Override public void onNext(VS value) {
      subject.onNext(value);
  }

  @Override public void onError(Throwable e) {
      throw new IllegalStateException(
              "ViewState observable must not reach error state - onError()", e);
  }

  @Override public void onComplete() {
      // ViewState observable never completes so ignore any complete event
  }
}
