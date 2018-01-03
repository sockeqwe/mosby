/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.mvp;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import java.lang.ref.WeakReference;

/**
 * A base implementation of a {@link MvpPresenter} that uses a <b>WeakReference</b> for referring
 * to the attached view.
 * <p>
 * Use {@link #ifViewAttached(ViewAction)} to interact with the view. Since the view is passive,
 * usually a Presenter should not get any data from View: so calls like view.getUserId() should not be done.
 * Rather write a method in your Presenter that takes the user id as parameter like this:
 * {@code
 *  void doSomething(int userId){
 *    // do something
 *    ...
 *
 *    ifViewAttached( view -> view.showSuccessful())
 *  }
 *
 * }
 * </p>
 *
 *
 * @param <V> type of the {@link MvpView}
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

  /**
   * An Action that is executed to interact with the view.
   * Usually a Presenter should not get any data from View: so calls like view.getUserId() should not be done.
   * Rather write a method in your Presenter that takes the user id as parameter like this:
   * {@code
   *  void doSomething(int userId){
   *    // do something
   *    ...
   *
   *    ifViewAttached( view -> view.showSuccessful())
   *  }
   * @param <V> The Type of the View
   */
  public interface ViewAction<V> {

    /**
     * This method will be invoked to run the action. Implement this method to interact with the view.
     * @param view The reference to the view. Not null.
     */
    void run(@NonNull V view);
  }

  private WeakReference<V> viewRef;
  private boolean presenterDestroyed = false;

  @UiThread @Override public void attachView(V view) {
    viewRef = new WeakReference<V>(view);
    presenterDestroyed = false;
  }

  /**
   * Gets the attached view. You should always call {@link #isViewAttached()} to check if the view
   * is attached to avoid NullPointerExceptions.
   *
   * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
   * @deprecated  Use {@link #ifViewAttached(ViewAction)}
   */
  @Deprecated @UiThread public V getView() {
    return viewRef == null ? null : viewRef.get();
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before
   * calling {@link #getView()} to get the view instance.
   * @deprecated  Use {@link #ifViewAttached(ViewAction)}
   */
  @Deprecated @UiThread public boolean isViewAttached() {
    return viewRef != null && viewRef.get() != null;
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated @UiThread @Override public void detachView(boolean retainInstance) {
  }

  /**
   * Executes the passed Action only if the View is attached.
   * If no View is attached, either an exception is thrown (if parameter exceptionIfViewNotAttached
   * is true) or the action is just not executed (no exception thrown).
   * Note that if no view is attached, this will not re-execute the given action if the View gets
   * re-attached.
   *
   * @param exceptionIfViewNotAttached true, if an exception should be thrown if no view is
   * attached while trying to execute the action. false, if no exception should be thrown (the action
   * will not be executed since no view is attached)
   * @param action The {@link ViewAction} that will be executed if a view is attached. This is
   * where you call view.isLoading etc. Use the view reference passed as parameter to {@link
   * ViewAction#run(Object)} and not deprecated method {@link #getView()}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override public void detachView() {
    detachView(true);
    if (viewRef != null) {
      viewRef.clear();
      viewRef = null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override public void destroy() {
    detachView(false);
    presenterDestroyed = true;
  }
}
