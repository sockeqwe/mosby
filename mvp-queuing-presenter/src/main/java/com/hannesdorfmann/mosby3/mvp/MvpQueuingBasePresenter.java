package com.hannesdorfmann.mosby3.mvp;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Presenter implementation queues ({@link ViewAction} to interact with the view layer if no
 * view is attached by using {@link #onceViewAttached(ViewAction)}.
 * Once a view is (re)attached, the queued {@link ViewAction} will be executed one after another.
 *
 * In the rare case you don't want to queue the any {@link ViewAction} but rather execute it once only
 * use {@link #ifViewAttached(ViewAction)}.
 *
 * @author Hannes Dorfmann
 * @since 3.1.0
 */
public class MvpQueuingBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    /**
     * An Action that is executed to interact with the view.
     * Usually a Presenter should not get any data from View: so calls like view.getUserId() should
     * not be done.
     * Rather write a method in your Presenter that takes the user id as parameter like this:
     * {@code
     * void doSomething(int userId){
     * // do something
     * ...
     * <p>
     * ifViewAttached( view -> view.showSuccessful())
     * }
     *
     * @param <V> The Type of the View
     */
    public interface ViewAction<V> {

        /**
         * This method will be invoked to run the action. Implement this method to interact with the
         * view.
         *
         * @param view The reference to the view. Not null.
         */
        void run(@NonNull V view);
    }

    private Queue<ViewAction<V>> viewActionQueue = new ConcurrentLinkedQueue<>();

    private WeakReference<V> viewRef;

    private boolean presenterDestroyed;

    /**
     * {@inheritDoc}
     */
    @Override
    public void attachView(@NonNull V view) {
        presenterDestroyed = false;
        viewRef = new WeakReference<V>(view);
        runQueuedActions();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void detachView(boolean retainInstance) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detachView() {
        viewRef.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        viewActionQueue.clear();
        presenterDestroyed = false;
    }

    /**
     * If the view is attached, the view action is run immediately.
     * If view is not attached, the action will be put in a queue. Once a view is attached, each
     * action will be polled from the queue and executed one after another.
     * <p>
     * You have to call this method from Android's main UI thread.
     *
     * @param action The action that should run to interact with the view
     */
    @MainThread
    protected final void onceViewAttached(ViewAction<V> action) {
        viewActionQueue.add(action);
        runQueuedActions();
    }

    /**
     * Runs the queued actions.
     */
    private void runQueuedActions() {
        V view = viewRef == null ? null : viewRef.get();
        if (view != null) {
            while (!viewActionQueue.isEmpty()) {
                ViewAction<V> action = viewActionQueue.poll();
                action.run(view);
            }
        }
    }

    /**
     * Executes the passed Action only if a View is attached.
     * if no view is attached either an exception will be thrown if  parameter
     * exceptionIfViewNotAttached is true. Otherwise, the action is just not executed (no exception
     * thrown).
     * Note that if no view is attached this will not re-executed the given action if the View get's
     * re attached. If you want to do that use {@link #onceViewAttached(ViewAction)}
     *
     * @param exceptionIfViewNotAttached true, if an exception should be thrown if no view is
     *                                   attached
     *                                   while trying to execute the action. false, if no exception should be thrown (but action will
     *                                   not executed either since no view attached)
     * @param action                     The {@link ViewAction} that will be exceuted if a view is attached. Here is
     *                                   where
     *                                   you call view.isLoading etc. Use the view reference passed as parameter to {@link
     *                                   ViewAction#run(Object)}.
     */
    @MainThread
    protected final void ifViewAttached(boolean exceptionIfViewNotAttached, ViewAction<V> action) {
        final V view = viewRef == null ? null : viewRef.get();
        if (view != null) {
            action.run(view);
        } else if (exceptionIfViewNotAttached) {
            throw new IllegalStateException(
                    "No View attached to Presenter. Presenter destroyed = " + presenterDestroyed);
        }
    }


    /**
     * Calls {@link #ifViewAttached(boolean, ViewAction)} with false as first parameter (don't throw
     * exception if view not attached).
     *
     * @see #ifViewAttached(boolean, ViewAction)
     */
    protected final void ifViewAttached(ViewAction<V> action) {
        ifViewAttached(false, action);
    }


}
