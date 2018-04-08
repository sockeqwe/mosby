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

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import io.reactivex.subjects.UnicastSubject;

/**
 * This type of presenter is responsible for interaction with the viewState in a Model-View-Intent way.
 * It is the bridge that is responsible for setting up the reactive flow between "view" and "model".
 * <p>
 * <p>
 * The methods {@link #bindIntents()} and {@link #unbindIntents()} are kind of representing the
 * lifecycle of this Presenter.
 * <ul>
 * <li>{@link #bindIntents()} is called the first time the view is attached </li>
 * <li>{@link #unbindIntents()} is called once the view is detached permanently because the view
 * has been destroyed and hence this presenter is not needed anymore and will also be destroyed
 * afterwards too.</li>
 * </ul>
 * </p>
 * <p>
 * <p>
 * This means that a presenter can survive orientation changes. During orientation changes (or when
 * the view is put on the back stack because the user navigated to another view) the view
 * will be detached temporarily and reattached to the presenter afterwards. To avoid memory leaks
 * this Presenter class offers two methods:
 * <ul>
 * <li>{@link #intent(ViewIntentBinder)}</li>: Use this to bind an Observable intent from the view
 * <li>{@link #subscribeViewState(Observable, ViewStateConsumer)}: Use this to bind the ViewState
 * (a viewState is an object (typically a POJO) that holds all the data the view needs to display</li>
 * </ul>
 * <p>
 * By using {@link #intent(ViewIntentBinder)} and {@link #subscribeViewState(Observable, ViewStateConsumer)}
 * a relay will be established between the view and this presenter that allows the view to be
 * temporarily detached, without unsubscribing the underlying reactive business logic workflow and
 * without causing memory leaks (caused by recreation of the view).
 * </p>
 * <p>
 * <p>
 * Please note that the methods {@link #attachView(MvpView)} and {@link #detachView(boolean)}
 * should not be overridden unless you have a really good reason to do so. Usually {@link #bindIntents()}
 * and {@link #unbindIntents()} should be enough.
 * </p>
 * <p>
 * <p>
 * In very rare cases you could also use {@link #getViewStateObservable()} to offer an observable
 * to other components you can make this method public.
 * </p>
 * <p>
 * <p>
 * <b>Please note that you should not reuse an MviBasePresenter once the View that originally has
 * instantiated this Presenter has been destroyed permanently</b>. App-wide singletons for
 * Presenters is not a good idea in Model-View-Intent. Reusing singleton-scoped Presenters for
 * different view instances may cause emitting the previous state of the previous attached view
 * (which already has been destroyed permanently).
 * </p>
 *
 * @param <V>  The type of the view this presenter responds to
 * @param <VS> The type of the viewState state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public abstract class MviBasePresenter<V extends MvpView, VS> implements MviPresenter<V, VS> {

    /**
     * The binder is responsible for binding a single view intent.
     * Typically, you use that in {@link #bindIntents()} in combination with the {@link
     * #intent(ViewIntentBinder)} function like this:
     * <pre><code>
     *   Observable<Boolean> loadIntent = intent(new ViewIntentBinder() {
     *      @Override
     *      public Observable<Boolean> bind(MyView view){
     *         return view.loadIntent();
     *      }
     *   }
     * </code></pre>
     *
     * @param <V> The View type
     * @param <I> The type of the Intent
     */
    protected interface ViewIntentBinder<V extends MvpView, I> {
        @NonNull
        Observable<I> bind(@NonNull V view);
    }

    /**
     * This "binder" is responsible for binding the view state to the currently attached view.
     * This typically "renders" the view.
     * <p>
     * Typically, this is used in {@link #bindIntents()} with
     * {@link #subscribeViewState(Observable, ViewStateConsumer)} like this:
     * <pre><code>
     *   Observable<MyViewState> viewState =  ... ;
     *   subscribeViewStateConsumerActually(viewState, new ViewStateConsumer() {
     *      @Override
     *      public void accept(MyView view, MyViewState viewState){
     *         view.render(viewState);
     *      }
     *   }
     * </code></pre>
     *
     * @param <V>  The view type
     * @param <VS> The ViewState type
     */
    protected interface ViewStateConsumer<V extends MvpView, VS> {
        void accept(@NonNull V view, @NonNull VS viewState);
    }

    /**
     * A simple class that holds a pair of the Intent relay and the binder to bind the actual Intent
     * Observable.
     *
     * @param <I> The Intent type
     */
    private class IntentRelayBinderPair<I> {
        private final Subject<I> intentRelaySubject;
        private final ViewIntentBinder<V, I> intentBinder;

        public IntentRelayBinderPair(Subject<I> intentRelaySubject,
                                     ViewIntentBinder<V, I> intentBinder) {
            this.intentRelaySubject = intentRelaySubject;
            this.intentBinder = intentBinder;
        }
    }

    /**
     * This relay is the bridge to the viewState (UI). Whenever the viewState gets re-attached, the
     * latest state will be re-emitted.
     */
    private final BehaviorSubject<VS> viewStateBehaviorSubject;

    /**
     * We only allow to call {@link #subscribeViewState(Observable, ViewStateConsumer)} method once
     */
    private boolean subscribeViewStateMethodCalled = false;
    /**
     * List of internal relays, bridging the gap between intents coming from the viewState (will be
     * unsubscribed temporarily when viewState is detached i.e. during config changes)
     */
    private List<IntentRelayBinderPair<?>> intentRelaysBinders = new ArrayList<>(4);

    /**
     * Composite Disposables holding subscriptions to all intents observable offered by the viewState.
     */
    private CompositeDisposable intentDisposables;

    /**
     * Disposable to unsubscribe from the viewState when the viewState is detached (i.e. during screen
     * orientation changes)
     */
    private Disposable viewRelayConsumerDisposable;

    /**
     * Disposable between the viewState observable returned from {@link #intent(ViewIntentBinder)}
     * and {@link #viewStateBehaviorSubject}
     */
    private Disposable viewStateDisposable;

    /**
     * Used to determine whether or not a View has been attached for the first time.
     * This is used to determine whether or not the intents should be bound via {@link
     * #bindIntents()} or rebound internally.
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
        viewStateBehaviorSubject = BehaviorSubject.create();
        reset();
    }

    /**
     * Creates a new Presenter with the initial view state
     *
     * @param initialViewState initial view state (must be not null)
     */
    public MviBasePresenter(@NonNull VS initialViewState) {
        if (initialViewState == null) {
            throw new NullPointerException("Initial ViewState == null");
        }

        viewStateBehaviorSubject = BehaviorSubject.createDefault(initialViewState);
        reset();
    }

    /**
     * Gets the view state observable.
     * <p>
     * Most likely you will use this method for unit testing your presenter.
     * </p>
     * <p>
     * <p>
     * In some very rare case it could be useful to provide other components, such as other presenters,
     * access to the state. This observable contains the same value as the one from {@link
     * #subscribeViewState(Observable, ViewStateConsumer)} which is also used to render the view.
     * In other words, this observable also represents the state of the View, so you could subscribe
     * via this observable to the view's state.
     * </p>
     *
     * @return Observable
     */
    protected Observable<VS> getViewStateObservable() {
        return viewStateBehaviorSubject;
    }

    @CallSuper
    @Override
    public void attachView(@NonNull V view) {
        if (viewAttachedFirstTime) {
            bindIntents();
        }

        //
        // Build the chain from bottom to top:
        // 1. Subscribe to ViewState
        // 2. Subscribe intents
        //
        if (viewStateConsumer != null) {
            subscribeViewStateConsumerActually(view);
        }

        int intentsSize = intentRelaysBinders.size();
        for (int i = 0; i < intentsSize; i++) {
            IntentRelayBinderPair<?> intentRelayBinderPair = intentRelaysBinders.get(i);
            bindIntentActually(view, intentRelayBinderPair);
        }

        viewAttachedFirstTime = false;
    }

    @Override
    @CallSuper
    public void detachView() {
        detachView(true);
        if (viewRelayConsumerDisposable != null) {
            // Cancel subscription from View to viewState Relay
            viewRelayConsumerDisposable.dispose();
            viewRelayConsumerDisposable = null;
        }

        if (intentDisposables != null) {
            // Cancel subscriptions from view intents to intent Relays
            intentDisposables.dispose();
            intentDisposables = null;
        }
    }

    @Override
    @CallSuper
    public void destroy() {
        detachView(false);
        if (viewStateDisposable != null) {
            // Cancel the overall observable stream
            viewStateDisposable.dispose();
        }

        unbindIntents();
        reset();
        // TODO should we re-emit the initial state? What if no initial state has been set?
        // TODO should we rather throw an exception if presenter is reused after view has been detached permanently
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    @CallSuper
    public void detachView(boolean retainInstance) {
    }

    /**
     * This is called when the View has been detached permanently (view is destroyed permanently)
     * to reset the internal state of this Presenter to be ready for being reused (even though
     * reusing presenters after their view has been destroy is BAD)
     */
    private void reset() {
        viewAttachedFirstTime = true;
        intentRelaysBinders.clear();
        subscribeViewStateMethodCalled = false;
    }

    /**
     * This method subscribes the Observable emitting {@code ViewState} over time to the passed
     * consumer.
     * <b>Only invoke this method once! Typically, in {@link #bindIntents()}</b>
     * <p>
     * Internally, Mosby will hold some relays to ensure that no items emitted from the ViewState
     * Observable will be lost while viewState is not attached nor that the subscriptions to
     * viewState intents will cause memory leaks while viewState detached.
     * </p>
     * <p>
     * Typically, this method is used in {@link #bindIntents()}  like this:
     * <pre><code>
     *   Observable<MyViewState> viewState =  ... ;
     *   subscribeViewStateConsumerActually(viewState, new ViewStateConsumer() {
     *      @Override
     *      public void accept(MyView view, MyViewState viewState){
     *         view.render(viewState);
     *      }
     *   }
     * </code></pre>
     *
     * @param viewStateObservable The Observable emitting new ViewState. Typically, an intent {@link
     *                            #intent(ViewIntentBinder)} causes the underlying business logic to do a change and eventually
     *                            create a new ViewState.
     * @param consumer            {@link ViewStateConsumer} The consumer that will update ("render") the view.
     */
    @MainThread
    protected void subscribeViewState(@NonNull Observable<VS> viewStateObservable,
                                      @NonNull ViewStateConsumer<V, VS> consumer) {
        if (subscribeViewStateMethodCalled) {
            throw new IllegalStateException(
                    "subscribeViewState() method is only allowed to be called once");
        }
        subscribeViewStateMethodCalled = true;

        if (viewStateObservable == null) {
            throw new NullPointerException("ViewState Observable is null");
        }

        if (consumer == null) {
            throw new NullPointerException("ViewStateBinder is null");
        }

        this.viewStateConsumer = consumer;

        viewStateDisposable = viewStateObservable.subscribeWith(
                new DisposableViewStateObserver<>(viewStateBehaviorSubject));
    }

    /**
     * Actually subscribes the view as consumer to the internally view relay.
     *
     * @param view The mvp view
     */
    @MainThread
    private void subscribeViewStateConsumerActually(@NonNull final V view) {

        if (view == null) {
            throw new NullPointerException("View is null");
        }

        if (viewStateConsumer == null) {
            throw new NullPointerException(ViewStateConsumer.class.getSimpleName()
                    + " is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
        }

        viewRelayConsumerDisposable = viewStateBehaviorSubject.subscribe(new Consumer<VS>() {
            @Override
            public void accept(VS vs) throws Exception {
                viewStateConsumer.accept(view, vs);
            }
        });
    }

    /**
     * This method is called once the view is attached to this presenter for the very first time.
     * For instance, it will not be called again during screen orientation changes when the view will be
     * detached temporarily.
     * <p>
     * <p>
     * The counter part of this method is {@link #unbindIntents()}.
     * This {@link #bindIntents()} and {@link #unbindIntents()} are kind of representing the
     * lifecycle of this Presenter.
     * {@link #bindIntents()} is called the first time the view is attached
     * and {@link #unbindIntents()} is called once the view is detached permanently because it has
     * been destroyed and hence this presenter is not needed anymore and will also be destroyed
     * afterwards
     * </p>
     */
    @MainThread
    abstract protected void bindIntents();

    /**
     * This method will be called once the view has been detached permanently and hence the presenter
     * will be "destroyed" too. This is the correct time for doing some cleanup like unsubscribe from
     * RxSubscriptions, etc.
     * <p>
     * <p>
     * The counter part of this method is {@link #bindIntents()} ()}.
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
     * no memory leak by using a {@link ViewIntentBinder} is caused by the subscription to the original
     * view's intent when the view gets detached.
     * <p>
     * Typically, this method is used in {@link #bindIntents()} like this:
     * <pre><code>
     *   Observable<Boolean> loadIntent = intent(new ViewIntentBinder() {
     *      @Override
     *      public Observable<Boolean> bind(MyView view){
     *         return view.loadIntent();
     *      }
     *   }
     * </code></pre>
     *
     * @param binder The {@link ViewIntentBinder} from where the the real view's intent will be
     *               bound
     * @param <I>    The type of the intent
     * @return The decorated intent Observable emitting the intent
     */
    @MainThread
    protected <I> Observable<I> intent(ViewIntentBinder<V, I> binder) {
        Subject<I> intentRelay = UnicastSubject.create();
        intentRelaysBinders.add(new IntentRelayBinderPair<I>(intentRelay, binder));
        return intentRelay;
    }

    @MainThread
    private <I> Observable<I> bindIntentActually(@NonNull V view,
                                                 @NonNull IntentRelayBinderPair<?> relayBinderPair) {

        if (view == null) {
            throw new NullPointerException(
                    "View is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
        }

        if (relayBinderPair == null) {
            throw new NullPointerException(
                    "IntentRelayBinderPair is null. This is a Mosby internal bug. Please file an issue at https://github.com/sockeqwe/mosby/issues");
        }

        Subject<I> intentRelay = (Subject<I>) relayBinderPair.intentRelaySubject;
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

        if (intentDisposables == null) {
            intentDisposables = new CompositeDisposable();
        }

        intentDisposables.add(intent.subscribeWith(new DisposableIntentObserver<I>(intentRelay)));
        return intentRelay;
    }
}
