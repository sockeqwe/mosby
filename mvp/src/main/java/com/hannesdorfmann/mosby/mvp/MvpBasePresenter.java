package com.hannesdorfmann.mosby.mvp;

import java.lang.ref.WeakReference;

/**
 * A base implementation of a {@link MvpPresenter} that uses a <b>WeakReference</b> for referring
 * to the attached view.
 * <p>
 * You should always check {@link #isViewAttached()} to check if the view is attached to this
 * presenter before calling {@link #getView()} to access the view.
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

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
    viewRef.clear();
    viewRef = null;
  }
}
