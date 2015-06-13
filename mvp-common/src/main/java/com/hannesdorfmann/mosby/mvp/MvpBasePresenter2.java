package com.hannesdorfmann.mosby.mvp;

import java.lang.ref.WeakReference;

public class MvpBasePresenter2<V extends MvpView> implements MvpPresenter<V> {

  private WeakReference<V> viewRef;

  @Override
  public void attachView(V view) {
    viewRef = new WeakReference<V>(view);
  }

  protected V getView() {
    if (viewRef == null) {
      throw new NullPointerException("MvpView reference is null. Have you called attachView()?");
    }
    return viewRef.get();
  }

  @Override
  public void detachView(boolean retainInstance) {
    if (viewRef != null) {
      //noinspection unchecked,ConstantConditions
      Class<V> viewClass = (Class<V>) viewRef.get().getClass().getGenericInterfaces()[0];
      V noOp = NoOp.of(viewClass);
      viewRef = new WeakReference<V>(noOp);
    }
  }
}
