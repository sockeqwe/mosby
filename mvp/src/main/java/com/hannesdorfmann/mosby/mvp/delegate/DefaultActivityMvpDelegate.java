package com.hannesdorfmann.mosby.mvp.delegate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * The concrete implementation of {@link}
 *
 * @author Hannes Dorfmann
 * @see ActivityMvpDelegate
 * @since 1.1.0
 */
public class DefaultActivityMvpDelegate<V extends MvpView, P extends MvpPresenter<V>>
    implements ActivityMvpDelegate {

  private MvpInternalDelegate internalDelegate;

  public DefaultActivityMvpDelegate(MvpDelegateCallback<V, P> delegateCallback) {
    internalDelegate = new MvpInternalDelegate(delegateCallback);
  }

  @Override public void onCreate(Bundle bundle) {
    internalDelegate.createPresenter();
    internalDelegate.attachView();
  }

  @Override public void onDestroy() {
    internalDelegate.detachView();
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {

  }

  @Override public void onStart() {

  }

  @Override public void onStop() {

  }

  @Override public void onRestart() {

  }

  @Override public void onContentChanged() {

  }

  @Override public void onSaveInstanceState(Bundle outState) {

  }

  @Override public void onPostCreate(Bundle savedInstanceState) {

  }
}
