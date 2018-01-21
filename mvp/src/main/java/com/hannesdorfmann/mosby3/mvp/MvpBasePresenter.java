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

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

/**
 * A base implementation of a {@link MvpPresenter} that uses a <b>WeakReference</b> for referring
 * to the attached view.
 * <p>
 * You should always check {@link #isViewAttached()} to check if the view is attached to this
 * presenter before calling {@link #getView()} to access the view.
 * </p>
 *
 * <p>
 * <b>Why is this class using internally a WeakReference for referring to the attached view and why
 * do I have to use {@link #isViewAttached()} check?</b>
 * </p>
 * <p>
 * In a "perfect world" you wouldn't need this check nor a WeakReference. In Mosby <b>all
 * interaction
 * from Presenter to View must be executed on android's main UI thread</b>. Since screen
 * orientation changes are also executed on the main UI thread it is not possible to run into the
 * scenario where view is detached while presenter still wants to update the UI, right? However, we
 * are not living in a perfect world. Let's say you use an AsyncTask to make an http request and
 * the Presenter gets the result back and updates the View. If you forget to cancel this AsyncTask
 * after the View has been destroyed (i.e. android back button pressed, {@link
 * MvpPresenter#detachView(boolean)} will be called) then you can run into the scenario that the
 * View is detached from Presenter and then the Presenter gets the result back from AsyncTask and
 * wants to update the View which is null (because detached). So the `isViewAttached()` check is
 * basically some kind of safety net. In a perfect implementation you wouldn't need the {@link
 * #isViewAttached()} check.
 * </p>
 * <p>
 * Furthermore, in a perfect world you wouldn't need a WeakReference for referring to the View. In
 * Mosby you can create your own MvpDelegate to change Mosby's default implementation and
 * behaviour.
 * We have decided to use a WeakReference to avoid memory leaks if you use a custom MvpDelegate
 * that is not implementing the contract of attaching and detaching View from Presenter
 * properly (i.e. don't detach view in Activity.onDestroy() ).
 * </p>
 * <p>
 * So using a WeakReference and adding the {@link #isViewAttached()} check are basically just some
 * kind of safety net and not needed in a "perfect world". Please note that if you are sure that
 * you are coding in such a "perfect world" then you can also think about implementing your own
 * Presenter without WeakReference and isViewAttached(). Note also that
 * {@link MvpPresenter} is an interface. Hence implementing you own Presenter is easy.
 * </p>
 *
 * @param <V> type of the {@link MvpView}
 * @author Hannes Dorfmann
 * @see MvpNullObjectBasePresenter
 * @since 1.0.0
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

  private WeakReference<V> viewRef;

  @UiThread
  @Override public void attachView(V view) {
    viewRef = new WeakReference<V>(view);
  }

  /**
   * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
   * is
   * attached to avoid NullPointerExceptions.
   *
   * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
   */
  @UiThread
  @Nullable public V getView() {
    return viewRef == null ? null : viewRef.get();
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before
   * calling {@link #getView()} to get the view instance.
   */
  @UiThread
  public boolean isViewAttached() {
    return viewRef != null && viewRef.get() != null;
  }

  @UiThread
  @Override public void detachView(boolean retainInstance) {
    if (viewRef != null) {
      viewRef.clear();
      viewRef = null;
    }
  }
}
