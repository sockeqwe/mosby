package com.hannesdorfmann.mosby3.mvi;

import android.support.annotation.CallSuper;
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
 * "view" and "model".
 *
 * <p>
 * Thee methods {@link #bindIntents()} and {@link #unbindIntents()} are kind of representing the
 * lifecycle of this Presenter.
 * <ul>
 * <li>{@link #bindIntents()} is called the first time the view is attached </li>
 * <li>{@link #unbindIntents()} is called once the view is detached permanently because the view
 * has
 * been destroyed and hence this presenter is not needed anymore and will also be destroyed
 * afterwards too.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This means that a presenter can survive orientation changes. During orientation changes (or when
 * the view is put on the back stack because the user navigated to another view) the view
 * will be detached temporarily and reattached to the presenter afterwards. To avoid memory leaks
 * this Presenter class offers two methods:
 * <ul>
 * <li>{@link #intent(ViewIntentBinder)}</li>: Use this to bind an Observable intent from the view
 * <li>{@link #subscribeViewState(Observable, ViewStateConsumer)}: Use this to bind the ViewState
 * (a
 * viewState is a object (typically a POJO) that holds all the data the view needs to display</li>
 * </ul>
 *
 * By using {@link #intent(ViewIntentBinder)} and {@link #subscribeViewState(Observable,
 * ViewStateConsumer)}
 * a relay will be established between the view and this presenter that allows the view to be
 * temporarily detached, without unsubscribing the underlyings reactive business logic workflow and
 * without causing memory leaks (caused by recreation of the view).
 * </p>
 *
 * <p>
 * Please note that the methods {@link #attachView(MvpView)} and {@link #detachView(boolean)}
 * should
 * not be overridden unless you have a really good reason to do so. Usually {@link #bindIntents()}
 * and {@link #unbindIntents()} should be enough.
 * </p>
 *
 * @param <V> The type of the viewState this presenter responds to
 * @param <VS> The type of the viewState state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public abstract class MviBasePresenter<V extends MvpView, VS> implements MviPresenter<V, VS> {

  /**
   * The binder is responsible to bind a single view intent.
   * Call <pre><code>
   *   // TODO example
   * </code></pre>
   *
   * @param <V> The View type
   * @param <I> The type of the Intent
   */
  protected interface ViewIntentBinder<V extends MvpView, I> {

    @NonNull public Observable<I> bind(@NonNull V view);
  }

  /**
   * This "binder" is responsible to bind the view state to the currently attached view.
   * This typically "renders" the view.
   *
   * @param <V> The view Type
   * @param <VS> The ViewState type
   */
  protected interface ViewStateConsumer<V extends MvpView, VS> {
    public void accept(@NonNull V view, @NonNull VS viewState);
  }

  /**
   * A simple class that holds a pair of the intent relay and the binder to bind the actual Intent
   * Observable.
   *
   * @param <I> The Intent type
   */
  private class IntentRelayBinderPair<I> {
    private final PublishSubject<I> intentRelaySubject;
    private final ViewIntentBinder<V, I> intentBinder;

    public IntentRelayBinderPair(PublishSubject<I> intentRelaySubject,
        ViewIntentBinder<V, I> intentBinder) {
      this.intentRelaySubject = intentRelaySubject;
      this.intentBinder = intentBinder;
    }
  }

  /**
   * This relay is the bridge to the viewState (UI). Whenever the viewState get's reattached, the
   * latest
   * state will be reemitted.
   */
  private final BehaviorSubject<VS> viewRelay;

  /**
   * We only allow to cal {@link #subscribeViewState(Observable, ViewStateConsumer)} method once
   */
  private boolean subscribeViewStateMethodCalled = false;
  /**
   * List of internal relays, bridging the gap between intents coming from the viewState (will be
   * unsubscribed temporarly when viewState is detached i.e. during config changes)
   */
  private List<IntentRelayBinderPair<?>> intentRelays = new ArrayList<>(4);

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
   * Disposable between the viewState observable returned from {@link #intent(ViewIntentBinder)}
   * and
   * {@link #viewRelay}
   */
  private Disposable viewStateDisposable;

  /**
   * Will be used to determine whether or not a View has been attached for the first time.
   * This is used to determine whether or not the intents should be bound via {@link
   * #bindIntents()}
   * or rebound internally.
   */
  private boolean viewAttachedFirstTime = true;

  /**
   * This binder is used to subscribe the view's render method to render the ViewState in the view.
   */
  private ViewStateConsumer<V, VS> viewStateConsumer;

  /**
   * Creates a new Presenter without an initial view state
   */
  public MviBasePresenter() {
    viewRelay = BehaviorSubject.create();
  }

  /**
   * Creaes a new Presenter with the initial view state
   *
   * @param initialViewState initial view state (must be not null)
   */
  public MviBasePresenter(@NonNull VS initialViewState) {
    if (initialViewState == null) {
      throw new NullPointerException("Initia ViewState == null");
    }

    viewRelay = BehaviorSubject.createDefault(initialViewState);
  }

  @Override public Observable<VS> getViewStateObservable() {
    return viewRelay;
  }

  /**
   * This method subscribes the Observable emitting {@code ViewState} over time to the passed
   * consumer.
   * <b>Do only invoke this method once! Typically in {@link #bindIntents()}</b>
   * <p>
   * Internally Mosby will hold some relays to ensure that no items emitted from the ViewState
   * Observable will be lost while viewState is not attached nor that the subscriptions to
   * viewState
   * intents will cause memory leaks while viewState detached.
   * </p>
   *
   * @param viewStateObservable The Observable emitting new ViewState. Typically an intent {@link
   * #intent(ViewIntentBinder)} causes the underyling business logic to do a change and eventually
   * create a new ViewState.
   * @param consumer {@link ViewStateConsumer} The consumer that will update ("render") the view.
   */
  @MainThread protected void subscribeViewState(@NonNull Observable<VS> viewStateObservable,
      @NonNull ViewStateConsumer<V, VS> consumer) {
    if (subscribeViewStateMethodCalled) {
      throw new IllegalStateException(
          "subscribeViewState() method is only allowed to be called once");
    }

    if (viewStateObservable == null) {
      throw new NullPointerException("ViewState Observable is null");
    }

    if (consumer == null) {
      throw new NullPointerException("ViewStateBinder is null");
    }

    this.viewStateConsumer = consumer;

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

  /**
   * Actually subscribes the view as consumer to the internaly view relay
   *
   * @param view The mvp view
   */
  @MainThread private void subscribeViewStateConsumerActually(@NonNull final V view) {

    if (view == null) {
      throw new NullPointerException("View is null");
    }

    ViewStateConsumer<V, VS> vsb = viewStateConsumer;
    if (vsb == null) {
      throw new NullPointerException(ViewStateConsumer.class.getSimpleName()
          + " is null. This is a mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
    }

    viewRelayConsumerDisposable = viewRelay.subscribe(new Consumer<VS>() {
      @Override public void accept(VS vs) throws Exception {
        viewStateConsumer.accept(view, vs);
      }
    });
  }

  @CallSuper @Override public void attachView(@NonNull V view) {
    if (viewAttachedFirstTime) {
      bindIntents();
    }
    int intentsSize = intentRelays.size();
    for (int i = 0; i < intentsSize; i++) {
      IntentRelayBinderPair<?> intentRelayBinderPair = intentRelays.get(i);
      bindIntentActually(view, intentRelayBinderPair);
    }

    if (viewStateConsumer != null) {
      subscribeViewStateConsumerActually(view);
    }

    viewAttachedFirstTime = false;
  }

  @Override @CallSuper public void detachView(boolean retainInstance) {
    if (!retainInstance) {
      if (viewStateDisposable != null) {
        // Cancel the overall observable stream
        viewStateDisposable.dispose();
      }

      unbindIntents();
    }

    if (viewRelayConsumerDisposable != null) {
      // Cancel subscription from View to viewState Relay
      viewRelayConsumerDisposable.dispose();
      viewRelayConsumerDisposable = null;
    }

    if (intentDisposals != null) {
      // Cancel subscriptons from view intents to intent Relays
      intentDisposals.dispose();
      intentDisposals = null;
    }
  }

  /**
   * This method is called one the view is attached for the very first time to this presenter.
   * It will not called again for instance during screen orientation changes when the view will be
   * detached temporarily.
   *
   * <p>
   * The counter part of this method is  {@link #unbindIntents()}.
   * This {@link #bindIntents()} and {@link #unbindIntents()} are kind of representing the
   * lifecycle of this Presenter.
   * {@link #bindIntents()} is called the first time the view is attached
   * and {@link #unbindIntents()} is called once the view is detached permanently because it has
   * been destroyed and hence this presenter is not needed anymore and will also be destroyed
   * afterwards
   * </p>
   */
  @MainThread abstract protected void bindIntents();

  /**
   * This metho will be called once the view has been detached permanently and hence the presenter
   * will be "destroyed" too. This is the correct time for doing some cleanup like unsubscribe from
   * some RxSubscriptions etc.
   *
   * * <p>
   * The counter part of this method is  {@link #bindIntents()} ()}.
   * This {@link #bindIntents()} and {@link #unbindIntents()} are kind of representing the
   * lifecycle of this Presenter.
   * {@link #bindIntents()} is called the first time the view is attached
   * and {@link #unbindIntents()} is called once the view is detached permanently because it has
   * been destroyed and hence this presenter is not needed anymore and will also be destroyed
   * afterwards
   * </p>
   */
  protected void unbindIntents() {
  }

  /**
   * This method creates a decorator around the original view's "intent". This method ensures that
   * no
   * memoryleak by using a {@link ViewIntentBinder} is caused by the subscription to the original
   * view's intent when the view gets
   * detached.
   *
   * @param binder The {@link ViewIntentBinder} from where the the real view's intent will be
   * bound
   * @param <I> The type of the intent
   * @return The decorated intent Observable emitting the intent
   */
  @MainThread protected <I> Observable<I> intent(ViewIntentBinder<V, I> binder) {
    PublishSubject<I> intentRelay = PublishSubject.create();
    intentRelays.add(new IntentRelayBinderPair<I>(intentRelay, binder));
    return intentRelay;
  }

  @MainThread private <I> Observable<I> bindIntentActually(@NonNull V view,
      @NonNull IntentRelayBinderPair<?> relayBinderPair) {

    if (view == null) {
      throw new NullPointerException(
          "View is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
    }

    if (relayBinderPair == null) {
      throw new NullPointerException(
          "IntentRelayBinderPair is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
    }

    PublishSubject<I> intentRelay = (PublishSubject<I>) relayBinderPair.intentRelaySubject;
    if (intentRelay == null) {
      throw new NullPointerException(
          "IntentRelay from binderPair is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
    }

    ViewIntentBinder<V, I> intentBinder = (ViewIntentBinder<V, I>) relayBinderPair.intentBinder;
    if (intentBinder == null) {
      throw new NullPointerException(ViewIntentBinder.class.getSimpleName()
          + " is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
    }
    Observable<I> intent = intentBinder.bind(view);
    if (intent == null) {
      throw new NullPointerException(
          "Intent Observable returned from Binder " + intentBinder + " is null");
    }

    if (intentDisposals == null) {
      intentDisposals = new CompositeDisposable();
    }

    intentDisposals.add(intent.subscribeWith(new DisposableIntentObserver<I>(intentRelay)));
    return intentRelay;
  }
}
