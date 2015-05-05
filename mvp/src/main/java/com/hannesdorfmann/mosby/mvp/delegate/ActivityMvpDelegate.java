package com.hannesdorfmann.mosby.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A delegate for Activities to attach them to mosbies mvp.
 *
 * The following methods needs to be invoked at the corresponding activities lifecycle methods:
 * <ul>
 * <li>{@link #onCreate(Bundle)}</li>
 * <li>{@link #onDestroy()}</li>
 * <li>{@link #onPause()} </li>
 * <li>{@link #onResume()} </li>
 * <li>{@link #onStart()} </li>
 * <li>{@link #onStop()} </li>
 * <li>{@link #onRestart()} </li>
 * <li>{@link #onContentChanged()} </li>
 * <li>{@link #onSaveInstanceState(Bundle)} </li>
 * <li>{@link #onPostCreate(Bundle)} </li>
 * <li></li>
 * </ul>
 *
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public interface ActivityMvpDelegate<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * This method must be called from {@link Activity#onCreate(Bundle)}.
   * This method internally creates the presenter and attaches the view to it.
   */
  void onCreate(Bundle bundle);

  /**
   * This method must be called from {@link Activity#onDestroy()}}.
   * This method internally detaches the view from presenter
   */
  void onDestroy();

  /**
   * This method must be called from {@link Activity#onPause()}
   */
  void onPause();

  /**
   * This method must be called from {@link Activity#onResume()}
   */
  void onResume();

  /**
   * This method must be called from {@link Activity#onStart()}
   */
  void onStart();

  /**
   * This method must be called from {@link Activity#onStop()}
   */
  void onStop();

  /**
   * This method must be called from {@link Activity#onRestart()}
   */
  void onRestart();

  /**
   * This method must be called from {@link Activity#onContentChanged()}
   */
  void onContentChanged();

  /**
   * This method must be called from {@link Activity#onSaveInstanceState(Bundle)}
   */
  void onSaveInstanceState(Bundle outState);

  /**
   * This method must be called from {@link Activity#onPostCreate(Bundle)}
   */
  void onPostCreate(Bundle savedInstanceState);
}
