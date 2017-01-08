package com.hannesdorfmann.mosby3.mvi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.hannesdorfmann.mosby3.ActivityMviDelegate;
import com.hannesdorfmann.mosby3.ActivityMviDelegateImpl;
import com.hannesdorfmann.mosby3.MviDelegateCallback;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * This abstract class can be used to extend from to implement an Model-View-Intent pattern with
 * this activity as View and a {@link MviPresenter} to coordinate the viewState and the underlying
 * model
 * (business logic)
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
public abstract class MviActivity<V extends MvpView, P extends MviPresenter<V, ?>>
    extends AppCompatActivity implements MvpView, MviDelegateCallback<V, P> {

  private boolean isRestoringViewState = false;
  protected ActivityMviDelegate<V, P> mvpDelegate;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getMvpDelegate().onCreate(savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    getMvpDelegate().onDestroy();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getMvpDelegate().onSaveInstanceState(outState);
  }

  @Override protected void onPause() {
    super.onPause();
    getMvpDelegate().onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    getMvpDelegate().onResume();
  }

  @Override protected void onStart() {
    super.onStart();
    getMvpDelegate().onStart();
  }

  @Override protected void onStop() {
    super.onStop();
    getMvpDelegate().onStop();
  }

  @Override protected void onRestart() {
    super.onRestart();
    getMvpDelegate().onRestart();
  }

  @Override public void onContentChanged() {
    super.onContentChanged();
    getMvpDelegate().onContentChanged();
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    getMvpDelegate().onPostCreate(savedInstanceState);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this viewState
   */
  @NonNull public abstract P createPresenter();

  /**
   * Get the mvp delegate. This is internally used for creating presenter, attaching and detaching
   * viewState from presenter.
   *
   * <p><b>Please note that only one instance of mvp delegate should be used per Activity
   * instance</b>.
   * </p>
   *
   * <p>
   * Only override this method if you really know what you are doing.
   * </p>
   *
   * @return {@link ActivityMviDelegate}
   */
  @NonNull protected ActivityMviDelegate<V, P> getMvpDelegate() {
    if (mvpDelegate == null) {
      mvpDelegate = new ActivityMviDelegateImpl<V, P>(this, this);
    }

    return mvpDelegate;
  }

  @NonNull @Override public V getMvpView() {
    try {
      return (V) this;
    } catch (ClassCastException e) {
      Log.e(this.toString(),
          "Couldn't cast the View to the corresponding View interface. Most likely you forgot to add \"Activity implements YourMvpViewInterface\".");
      throw e;
    }
  }

  @Override public final Object onRetainCustomNonConfigurationInstance() {
    return getMvpDelegate().onRetainCustomNonConfigurationInstance();
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.isRestoringViewState = restoringViewState;
  }

  protected boolean isRestoringViewState() {
    return isRestoringViewState;
  }
}
