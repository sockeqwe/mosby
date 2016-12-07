package com.hannesdorfmann.mosby3.mvi;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;

/**
 * This type of presenter is responsible to interact with the view in a Model-View-Intent way.
 * A {@link MviBasePresenter} is the bridge that is repsonsible to setup the reactive flow between
 * view
 * and model
 *
 * @param <V> The type of the view this presenter responds to
 * @param <VS> The type of the view state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public abstract class MviBasePresenter<V extends MvpView, VS> implements MviPresenter<V, VS> {

  /**
   * This relay is the bridge to the view (UI). Whenever the view get's reattached, the latest
   * state will be reemitted.
   */
  private BehaviorSubject<VS> viewRelay;

  /**
   * We only allow to cal {@link #view(Object, Observable, Consumer)} method once
   */
  private boolean viewMethodCalled = false;
  /**
   * List of internal relays, bridging the gap between intents coming from the view (will be
   * unsubscribed temporarly when view is detached i.e. during config changes)
   */
  private List<IntentRelayObservablePair<?, VS>> intentModelRelays = new ArrayList<>(4);

  /**
   * Used for {@link #intentModelRelays}
   */
  private int intentModelRelayIndex = 0;

  /**
   * Composite Desposals holding subscriptions to all intents observable offered by the view.
   */
  private CompositeDisposable intentDisposals;

  /**
   * Disposal to unsubscribe from the view when the view is detached (i.e. during screen
   * orientation
   * changes)
   */
  private Disposable viewRelayConsumerDisposable;

  /**
   * Disposable between the viewState observable returned from {@link #model(Observable,
   * BiFunction)} and {@link #viewRelay}
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
   * Observable will be lost while view is not attached nor that the subscriptions to view intents
   * will cause memory leaks while view detached.
   * </p>
   *
   * @param initialViewState The initial ViewState. This item will emited for the first time
   * @param viewStateObservable The Observable emiting ViewState changes over time. This observable
   * must be created with {@link #model(Observable, BiFunction)} functionl
   * @param consumer The Consumer / subscriber to the viewState Observable
   */
  @MainThread protected void view(@NonNull VS initialViewState, Observable<VS> viewStateObservable,
      Consumer<VS> consumer) {

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
        viewRelay.onError(e); // TODO maybe some own error handling
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
   * This method is used to create an observable that receive "intents" and as result returns an
   * observable emiting the changed view state (changed over time).
   * Internally, this method uses {@link Observable#switchMap(Function)} for each emitted item from
   * the intent observable. Also, this method internally ensures that no memory leak is caused by
   * the subscription to the view's intent when the view gets detached
   *
   * @param intent The intent observable. Typically offered by the view interface
   * @param reducerFunction The reducer function that takes the old viewstate and the intent as
   * input and returns an observable for the view state.
   * @param <I> The type of the intent
   * @return The Observable emitting the new ViewState. This observable should be passed in as
   * parameter to {@link #view(Object, Observable, Consumer)} function
   */
  @MainThread protected <I> Observable<VS> model(final Observable<I> intent,
      final BiFunction<ViewState<VS>, I, ObservableSource<VS>> reducerFunction) {

    final PublishSubject<I> intentRelay;
    Observable<VS> modelObservable;
    if (intentModelRelayIndex < intentModelRelays.size()) {
      IntentRelayObservablePair<I, VS> intentReleayModelObservablePair =
          (IntentRelayObservablePair<I, VS>) intentModelRelays.get(intentModelRelayIndex);
      intentRelay = intentReleayModelObservablePair.intentRelay;
      if (intentRelay == null) {
        throw new IllegalStateException(
            "Somehow Mosby's internal view intent relay is null. The view intent was: " + intent);
      }
      modelObservable = intentReleayModelObservablePair.modelViewStateObservable;
      if (modelObservable == null) {
        throw new IllegalStateException(
            "Somehow Mosby's internal modelFunction observable is null. The view intent was "
                + intent);
      }
    } else {
      intentRelay = PublishSubject.create();
      modelObservable = intentRelay.switchMap(new Function<I, ObservableSource<VS>>() {
        @Override public ObservableSource<VS> apply(I intent) throws Exception {
          return reducerFunction.apply(currentViewState, intent);
        }
      });
      intentModelRelays.add(new IntentRelayObservablePair<I, VS>(intentRelay, modelObservable));
    }

    if (intentDisposals == null) {
      intentDisposals = new CompositeDisposable();
    }
    intentDisposals.add(intent.subscribeWith(new DisposableObserver<I>() {
      @Override public void onNext(I value) {
        intentRelay.onNext(value);
      }

      @Override public void onError(Throwable e) {
        intentRelay.onError(e);
      }

      @Override public void onComplete() {
        intentRelay.onComplete(); // TODO: Does an intent ever completes?
      }
    }));
    intentModelRelayIndex++;
    return modelObservable;
  }

  @Override public void detachView(boolean retainInstance) {
    if (!retainInstance) {
    }

    if (viewRelayConsumerDisposable != null) {
      viewRelayConsumerDisposable.dispose();
    }
    viewMethodCalled = false;
    intentModelRelayIndex = 0;
    intentDisposals.dispose();
    intentDisposals = null;
  }

  /**
   * This class just holds a pair of Intent relay
   *
   * @param <I> The views's intent relay
   * @param <VS> The ViewState Observable returned by the {@link #model(Observable, BiFunction)}
   * function
   */
  private final class IntentRelayObservablePair<I, VS> {
    private final PublishSubject<I> intentRelay;
    private final Observable<VS> modelViewStateObservable;

    public IntentRelayObservablePair(PublishSubject<I> intentRelay,
        Observable<VS> modelViewStateObservable) {
      this.intentRelay = intentRelay;
      this.modelViewStateObservable = modelViewStateObservable;
    }
  }
}
