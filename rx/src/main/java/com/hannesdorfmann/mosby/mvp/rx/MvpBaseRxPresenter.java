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

package com.hannesdorfmann.mosby.mvp.rx;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import java.lang.ref.WeakReference;
import rx.Subscriber;


/**
 * A base implementation of a {@link MvpPresenter} for RxJava. The presenter is a {@link
 * Subscriber}. Furthermore,
 * a <b>WeakReference</b>  is used for referring to the attached view.
 * <p>
 * You should always check {@link #isViewAttached()} to check if the view is attached to
 * this * presenter before calling {@link #getView()} to access the view.
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MvpBaseRxPresenter<V extends MvpView, M> extends Subscriber<M>
    implements MvpPresenter<V> {

  private WeakReference<V> viewRef;

  @Override public void setView(V view) {
    viewRef = new WeakReference<V>(view);
  }

  /**
   * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
   * is
   * attached to avoid NullPointerExceptions
   */
  protected V getView() {
    return viewRef.get();
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before
   * calling {@link #getView()} to get the view instance.
   */
  protected boolean isViewAttached() {
    return viewRef != null && viewRef.get() != null;
  }

  @Override public void onDestroy(boolean retainInstance) {
    if (viewRef != null) {
      viewRef.clear();
      viewRef = null;
    }

    if (!retainInstance) {
      unsubscribe();
    }
  }

  @Override public abstract void onCompleted();

  @Override public abstract void onError(Throwable e);

  @Override public abstract void onNext(M m);
}
