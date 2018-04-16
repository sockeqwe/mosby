package com.hannesdorfmann.mosby3.mvp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import com.hannesdorfmann.mosby3.mvp.delegate.FragmentMvpDelegate;
import com.hannesdorfmann.mosby3.mvp.delegate.FragmentMvpDelegateImpl;
import com.hannesdorfmann.mosby3.mvp.delegate.MvpDelegateCallback;

/**
 * A DialogFragment that uses an {@link MvpPresenter} to implement a Model-View-Presenter
 * architecture
 *
 * @author Hannes Dorfmann
 * @author Michał Seroczyński
 * @since 3.1.1
 */
public abstract class MvpDialogFragment<V extends MvpView, P extends MvpPresenter<V>> extends DialogFragment
    implements MvpDelegateCallback<V, P>, MvpView {

  protected FragmentMvpDelegate<V, P> mvpDelegate;

  /**
   * The presenter for this view. Will be instantiated with {@link #createPresenter()}
   */
  protected P presenter;

  /**
   * Creates a new presenter instance, if needed. Will reuse the previous presenter instance if
   * {@link #setRetainInstance(boolean)} is set to true. This method will be called from
   * {@link #onViewCreated(View, Bundle)}
   */
  public abstract P createPresenter();

  /**
   * * Get the mvp delegate. This is internally used for creating presenter, attaching and
   * detaching view from presenter.
   *
   * <p>
   * <b>Please note that only one instance of mvp delegate should be used per fragment
   * instance</b>.
   * </p>
   *
   * <p>
   * Only override this method if you really know what you are doing.
   * </p>
   *
   * @return {@link FragmentMvpDelegateImpl}
   */
  @NonNull protected FragmentMvpDelegate<V, P> getMvpDelegate() {
    if (mvpDelegate == null) {
      mvpDelegate = new FragmentMvpDelegateImpl<>(this, this, true, true);
    }

    return mvpDelegate;
  }

  @NonNull @Override public P getPresenter() {
    return presenter;
  }

  @Override public void setPresenter(@NonNull P presenter) {
    this.presenter = presenter;
  }

  @NonNull @Override public V getMvpView() {
    return (V) this;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getMvpDelegate().onViewCreated(view, savedInstanceState);
  }

  @Override public void onDestroyView() {
    Dialog dialog = getDialog();
    // Handles https://code.google.com/p/android/issues/detail?id=17423
    if (dialog != null && getRetainInstance()) {
      dialog.setDismissMessage(null);
    }

    super.onDestroyView();
    getMvpDelegate().onDestroyView();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getMvpDelegate().onCreate(savedInstanceState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    getMvpDelegate().onDestroy();
  }

  @Override public void onPause() {
    super.onPause();
    getMvpDelegate().onPause();
  }

  @Override public void onResume() {
    super.onResume();
    getMvpDelegate().onResume();
  }

  @Override public void onStart() {
    super.onStart();
    getMvpDelegate().onStart();
  }

  @Override public void onStop() {
    super.onStop();
    getMvpDelegate().onStop();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getMvpDelegate().onActivityCreated(savedInstanceState);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    getMvpDelegate().onAttach(activity);
  }

  @Override public void onDetach() {
    super.onDetach();
    getMvpDelegate().onDetach();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getMvpDelegate().onSaveInstanceState(outState);
  }

}