package com.hannesdorfmann.mosby3.mvp;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Presenter implementation queues ({@link ViewAction} to interact with the view layer if no
 * view is attached.
 * Once a view is (re)attached, the queued {@link ViewAction} will be executed one after another.
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
   *
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

  /**
   * {@inheritDoc}
   */
  @Override public void attachView(@NonNull V view) {
    viewRef = new WeakReference<V>(view);
    runQueuedActions();
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated @Override public void detachView(boolean retainInstance) {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void detachView() {
    viewRef.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void destroy() {
    viewActionQueue.clear();
  }

  /**
   * If the view is attached, the view action is run immediately.
   * If view is not attached, the action will be put in a queue. Once a view is attached, each
   * action will be polled from the queue and executed one after another.
   *
   * You have to call this method from Android's main UI thread.
   *
   * @param action The action that should run to interact with the view
   */
  @MainThread protected final void onceViewAttached(ViewAction<V> action) {
    viewActionQueue.add(action);
    runQueuedActions();
  }

  private void runQueuedActions() {
    V view = viewRef == null ? null : viewRef.get();
    if (view != null) {
      while (!viewActionQueue.isEmpty()) {
        ViewAction<V> action = viewActionQueue.poll();
        action.run(view);
      }
    }
  }
}
