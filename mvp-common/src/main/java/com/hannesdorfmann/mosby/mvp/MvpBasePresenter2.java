package com.hannesdorfmann.mosby.mvp;

public class MvpBasePresenter2<V extends MvpView> implements MvpPresenter<V> {

  private V view;

  @Override
  public void attachView(V view) {
    this.view = view;
  }

  protected V getView() {
    if (view == null) {
      throw new NullPointerException("MvpView reference is null. Have you called attachView()?");
    }
    return view;
  }

  @Override
  public void detachView(boolean retainInstance) {
    if (view != null) {
      //noinspection unchecked
      Class<V> viewClass = (Class<V>) view.getClass().getGenericInterfaces()[0];
      view = NoOp.of(viewClass);
    }
  }
}
