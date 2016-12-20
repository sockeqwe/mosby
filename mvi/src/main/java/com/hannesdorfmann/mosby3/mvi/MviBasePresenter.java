package com.hannesdorfmann.mosby3.mvi;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;

/**
 * This type of presenter is responsible to interact with the viewState in a Model-View-Intent way.
 * A {@link MviBasePresenter} is the bridge that is repsonsible to setup the reactive flow between
 * viewState
 * and model
 *
 * @param <V> The type of the viewState this presenter responds to
 * @param <VS> The type of the viewState state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public abstract class MviBasePresenter<V extends MvpView, VS> implements MviPresenter<V, VS> {

  /**
   * This relay is the bridge to the viewState (UI). Whenever the viewState get's reattached, the
   * latest
   * state will be reemitted.
   */
  private BehaviorSubject<VS> viewRelay;

  /**
   * We only allow to cal {@link #subscribeViewState(Object, Observable, Consumer)} method once
   */
  private boolean viewMethodCalled = false;
  /**
   * List of internal relays, bridging the gap between intents coming from the viewState (will be
   * unsubscribed temporarly when viewState is detached i.e. during config changes)
   */
  private List<PublishSubject<?>> intentRelays = new ArrayList<>(4);

  /**
   * Used for {@link #intentRelays}
   */
  private int intentRelayIndex = 0;

  /**
   * Composite Desposals holding subscriptions to all intents observable offered by the viewState.
   */
  private CompositeDisposable intentDisposals;

  /**
   * Disposal to unsubscribe from the viewState when the viewState is detached (i.e. during screen
   * orientation
   * changes)
   */
  private Disposable viewRelayConsumerDisposable;

  /**
   * Disposable between the viewState observable returned from {@link #intent(Observable)} and
   * {@link #viewRelay}
   */
  private Disposable viewStateDisposable;

  /**
   * Wrapper class to always access the latest viewstate value
   */
  private final ViewState<VS> currentViewState = new ViewState<VS>() {
    @Override VS get() {
      return viewRelay.getValue();
    }
  };

  /**
   * This method subscribes the Observable emiting {@link ViewState} over time to the passed
   * consumer.
   * <b>Do only invoke this method once!</b>
   * <p>
   * Internally Mosby will hold some relays to ensure that no items emitted from the ViewState
   * Observable will be lost while viewState is not attached nor that the subscriptions to viewState
   * intents
   * will cause memory leaks while viewState detached.
   * </p>
   *
   * @param initialViewState The initial ViewState. This item will emited for the first time
   * @param viewStateObservable The Observable emiting ViewState changes over time. This observable
   * must be created with {@link #intent(Observable)} functionl
   * @param consumer The Consumer / subscriber to the viewState Observable
   */
  @MainThread protected void subscribeViewState(@NonNull VS initialViewState,
      Observable<VS> viewStateObservable, Consumer<VS> consumer) {

    if (viewMethodCalled) {
      throw new IllegalStateException("View Method is only allowed to be called once");
    }

    if (initialViewState == null) {
      throw new NullPointerException("initialViewState == null");
    }

    if (viewRelay == null) {
      viewRelay = BehaviorSubject.createDefault(initialViewState);
    }

    if (viewRelayConsumerDisposable != null) {
      viewRelayConsumerDisposable.dispose();
    }

    viewRelayConsumerDisposable = viewRelay.subscribe(consumer);

    // TODO could disposing cause any issue with multithreading?
    // Unsubscribing on the main thread but receiving an emitted item on another thread
    // Maybe we should add an observeOn(AndroidSchedulers.mainThread()) automatically

    if (viewStateDisposable != null) {
      viewStateDisposable.dispose();
    }

    viewStateDisposable = viewStateObservable.subscribeWith(new DisposableObserver<VS>() {
      @Override public void onNext(VS value) {
        viewRelay.onNext(value);
      }

      @Override public void onError(Throwable e) {
        throw new IllegalStateException(
            "ViewState observable must not reach error state - onError()", e);
      }

      @Override public void onComplete() {
        // ViewState observable never completes so ignore any complete event
      }
    });
  }

  @Override public Observable<VS> getViewStateObservable() {
    return viewRelay;
  }

  /**
   * This method creates a decorator around the original "intent".This method ensures that no
   * memory
   * leak is caused by the subscription to the viewState's intent when the viewState gets detached
   *
   * @param intent The intent observable. Typically offered by the viewState interface
   * @param <I> The type of the intent
   * @return The decorated intent Observable emitting the intent
   */
  @MainThread protected <I> Observable<I> intent(final Observable<I> intent) {

    PublishSubject<I> intentRelay;
    if (intentRelayIndex < intentRelays.size()) {
      intentRelay = (PublishSubject<I>) intentRelays.get(intentRelayIndex);
      if (intentRelay == null) {
        throw new IllegalStateException(
            "Somehow Mosby's internal viewState intent relay is null. The viewState intent was: "
                + intent);
      }
    } else {
      intentRelay = PublishSubject.create();
      intentRelays.add(intentRelay);
    }

    if (intentDisposals == null) {
      intentDisposals = new CompositeDisposable();
    }

    intentDisposals.add(intent.subscribeWith(new DisposableIntentObserver<I>(intentRelay)));
    intentRelayIndex++;
    return intentRelay;
  }

  @Override public void detachView(boolean retainInstance) {
    if (!retainInstance) {
      if (viewStateDisposable != null) {
        viewStateDisposable.dispose();
      }
    }

    if (viewRelayConsumerDisposable != null) {
      viewRelayConsumerDisposable.dispose();
    }
    viewMethodCalled = false;
    intentRelayIndex = 0;
    if (intentDisposals != null) {
      intentDisposals.dispose();
      intentDisposals = null;
    }
  }
}
