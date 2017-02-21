/*
 * Copyright 2017 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import java.util.Map;

/**
 * This class basically represents a Map for View Id to the Presenter / ViewState.
 * One instance of this class is also associated by {@link PresenterManager} to one Activity (kept
 * across screen orientation changes)
 *
 * @author Hannes Dorfmann
 * @since 3.0.0
 */
class ActivityScopedCache {

  private final Map<String, PresenterHolder> presenterMap = new ArrayMap<>();

  ActivityScopedCache() {
    // package private
  }

  public void clear() {
    /*
    // TODO: can this check if there are still Presenters in the internal cache that must be detached? Maybe post() / postDelayed() on the  MainThreadLooper()
    // it doesn't work out that well, because super.onDestroy() which then invokes PresenterManager.clear() might be called before the MviDelegates did their job to remove the Presenter from cache and dettach the view permanently

    for (PresenterHolder holder : presenterMap.values()) {
      // This should never be the case: If there were some presenters left in the internal cache,
      // a delegate didn't work correctly as expected
      if (holder.presenter != null) {
        holder.presenter.detachView(false);
        if (PresenterManager.DEBUG) {
          Log.w(PresenterManager.DEBUG_TAG,
              "Found a Presenter that is still alive. This should never happen. It seems that a MvpDelegate / MviDelegate didn't work correctly because this Delegate should have removed the presenter. The Presenter was "
                  + holder.presenter);
        }
      }
    }
    */
    presenterMap.clear();
  }

  /**
   * Get the Presenter for a given {@link MvpView} if exists or <code>null</code>
   *
   * @param viewId The mosby internal view id
   * @param <P> The type tof the {@link MvpPresenter}
   * @return The Presenter for the given view id or <code>null</code>
   */
  @Nullable public <P> P getPresenter(@NonNull String viewId) {
    PresenterHolder holder = presenterMap.get(viewId);
    return holder == null ? null : (P) holder.presenter;
  }

  /**
   * Get the ViewState for a given {@link MvpView} if exists or <code>null</code>
   *
   * @param viewId The mosby internal view id
   * @param <VS> The type tof the {@link MvpPresenter}
   * @return The ViewState for the given view id or <code>null</code>
   */
  @Nullable public <VS> VS getViewState(@NonNull String viewId) {
    PresenterHolder holder = presenterMap.get(viewId);
    return holder == null ? null : (VS) holder.viewState;
  }

  /**
   * Put the presenter in the internal cache
   *
   * @param viewId The mosby internal View id of the {@link MvpView} which the presenter is
   * associated to.
   * @param presenter The Presenter
   */
  public void putPresenter(@NonNull String viewId,
      @NonNull MvpPresenter<? extends MvpView> presenter) {

    if (viewId == null) {
      throw new NullPointerException("ViewId is null");
    }

    if (presenter == null) {
      throw new NullPointerException("Presenter is null");
    }

    PresenterHolder presenterHolder = presenterMap.get(viewId);
    if (presenterHolder == null) {
      presenterHolder = new PresenterHolder();
      presenterHolder.presenter = presenter;
      presenterMap.put(viewId, presenterHolder);
    } else {
      presenterHolder.presenter = presenter;
    }
  }


  /**
   * Put the viewstate in the internal cache
   *
   * @param viewId The mosby internal View id of the {@link MvpView} which the presenter is
   * associated to.
   * @param viewState The Viewstate
   */
  public void putViewState(@NonNull String viewId,
      @NonNull Object viewState) {

    if (viewId == null) {
      throw new NullPointerException("ViewId is null");
    }

    if (viewState == null) {
      throw new NullPointerException("ViewState is null");
    }

    PresenterHolder presenterHolder = presenterMap.get(viewId);
    if (presenterHolder == null) {
      presenterHolder = new PresenterHolder();
      presenterHolder.viewState = viewState;
      presenterMap.put(viewId, presenterHolder);
    } else {
      presenterHolder.viewState = viewState;
    }
  }

  /**
   * Removes the Presenter (and ViewState) from the internal storage
   *
   * @param viewId The msoby internal view id
   */
  public void remove(@NonNull String viewId) {

    if (viewId == null) {
      throw new NullPointerException("View Id is null");
    }

    presenterMap.remove(viewId);
  }

  /**
   * Internal config change Cache entry
   */
  static final class PresenterHolder {
    private MvpPresenter<?> presenter;
    private Object viewState; // workaround: didn't want to introduce dependency to viewstate module
  }
}
