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

package com.hannesdorfmann.mosby.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A delegate for Fragments to attach them to mosby mvp.
 * <p>
 * The following methods must be invoked from the corresponding Fragments lifecycle methods:
 *
 * <ul>
 * <li>{@link #onCreate(Bundle)}</li>
 * <li>{@link #onDestroy()}</li>
 * <li>{@link #onPause()} </li>
 * <li>{@link #onResume()} </li>
 * <li>{@link #onStart()} </li>
 * <li>{@link #onStop()} </li>
 * <li>{@link #onViewCreated(View, Bundle)} </li>
 * <li>{@link #onActivityCreated(Bundle)} </li>
 * <li>{@link #onSaveInstanceState(Bundle)} </li>
 * <li>{@link #onAttach(Activity)} </li>
 * <li>{@link #onDetach()}</li>
 * <li></li>
 * </ul>
 * </p>
 *
 * @param <V> The type of {@link MvpView}
 * @param <P> The type of {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @since 1.1.0
 */
public interface FragmentMvpDelegate<V extends MvpView, P extends MvpPresenter<V>> {

  /**
   * Must be called from {@link Fragment#onCreate(Bundle)}
   *
   * @param saved The bundle
   */
  public void onCreate(Bundle saved);

  /**
   * Must be called from {@link Fragment#onDestroy()}
   */
  public void onDestroy();

  /**
   * Must be called from {@link Fragment#onViewCreated(View, Bundle)}
   *
   * @param view The inflated view
   * @param savedInstanceState the bundle with the viewstate
   */
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState);

  /**
   * Must be called from {@link Fragment#onDestroyView()}
   */
  public void onDestroyView();

  /**
   * Must be called from {@link Fragment#onPause()}
   */
  public void onPause();

  /**
   * Must be called from {@link Fragment#onResume()}
   */
  public void onResume();

  /**
   * Must be called from {@link Fragment#onStart()}
   */
  public void onStart();

  /**
   * Must be called from {@link Fragment#onStop()}
   */
  public void onStop();

  /**
   * Must be called from {@link Fragment#onActivityCreated(Bundle)}
   *
   * @param savedInstanceState The saved bundle
   */
  public void onActivityCreated(Bundle savedInstanceState);

  /**
   * Must be called from {@link Fragment#onAttach(Activity)}
   *
   * @param activity The activity the fragment is attached to
   */
  public void onAttach(Activity activity);

  /**
   * Must be called from {@link Fragment#onDetach()}
   */
  public void onDetach();

  /**
   * Must be called from {@link Fragment#onSaveInstanceState(Bundle)}
   */
  public void onSaveInstanceState(Bundle outState);
}
