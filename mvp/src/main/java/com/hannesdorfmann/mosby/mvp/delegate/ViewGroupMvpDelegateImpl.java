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

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * The default implementation of {@link ViewGroupMvpDelegate}
 *
 * @author Hannes Dorfmann
 * @see ViewGroupMvpDelegate
 * @since 1.1.0
 */
public class ViewGroupMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>>
    implements ViewGroupMvpDelegate<V, P> {

  // TODO allow custom save state hook in

  protected ViewGroupDelegateCallback<V, P> delegateCallback;
  protected OrientationChangeManager<V, P> orientationChangeManager =
      new OrientationChangeManager<>();
  protected int viewId = -1;

  public ViewGroupMvpDelegateImpl(ViewGroupDelegateCallback<V, P> delegateCallback) {
    if (delegateCallback == null) {
      throw new NullPointerException("MvpDelegateCallback is null!");
    }
    this.delegateCallback = delegateCallback;
  }

  @Override public void onAttachedToWindow() {

    // Try to reuse presenter instance from (before screen orientation changes)
    if (delegateCallback.isRetainInstance()) {
      P presenter = orientationChangeManager.getPresenter(viewId, delegateCallback.getContext());
      if (presenter != null) {
        delegateCallback.setPresenter(presenter);
        presenter.attachView(delegateCallback.getMvpView());
        return;
      }
    }

    // TODO clean up that part (double getPresenter check)
    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      presenter = delegateCallback.createPresenter();
    }
    if (presenter == null) {
      throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
    }

    delegateCallback.setPresenter(presenter);
    if (delegateCallback.isRetainInstance()) {
      viewId = orientationChangeManager.nextViewId(delegateCallback.getContext());
      orientationChangeManager.putPresenter(viewId, presenter, delegateCallback.getContext());
    }

    presenter.attachView(delegateCallback.getMvpView());
  }

  @Override public void onDetachedFromWindow() {

    if (delegateCallback.isRetainInstance()) {
      Context context = delegateCallback.getContext();

      boolean destroyedPermanently =
          orientationChangeManager.willViewBeDestroyedPermanently(context);

      if (destroyedPermanently) {
        // Whole activity will be destroyed
        // Internally Orientation manager already does the clean up
        viewId = 0;
        delegateCallback.getPresenter().detachView(false);
      } else {
        boolean detachedBecauseOrientationChange =
            orientationChangeManager.willViewBeDetachedBecauseOrientationChange(context);
        if (detachedBecauseOrientationChange) {
          // Simple orientation change
          delegateCallback.getPresenter().detachView(true);
        } else {
          // view detached, i.e. because of back stack / navigation
          orientationChangeManager.removePresenterAndViewState(viewId, context);
          viewId = 0;
          delegateCallback.getPresenter().detachView(false);
        }
      }
    } else {
      // retain instance feature disabled
      delegateCallback.getPresenter().detachView(false);
    }

    // Important cleanup to avoid memory leaks
    orientationChangeManager.cleanUp();
  }

  /**
   * Must be called from {@link View#onSaveInstanceState()}
   */
  public Parcelable onSaveInstanceState() {

    Parcelable superState = delegateCallback.superOnSaveInstanceState();

    if (delegateCallback.isRetainInstance()) {
      return createSavedState(superState);
    } else {
      return superState;
    }
  }

  /**
   * Create a MosbySavedState
   *
   * @return The saved state
   */
  protected MosbySavedState createSavedState(Parcelable superState) {
    MosbySavedState state = new MosbySavedState(superState);
    state.setMosbyViewId(viewId);
    return state;
  }

  /**
   * Restore the data from SavedState
   *
   * @param state The state to read data from
   */
  protected void restoreSavedState(MosbySavedState state) {
    viewId = state.getMosbyViewId();
  }

  /**
   * Must be called from {@link View#onRestoreInstanceState(Parcelable)}
   */
  public void onRestoreInstanceState(Parcelable state) {

    if (!(state instanceof MosbySavedState)) {
      delegateCallback.superOnRestoreInstanceState(state);
      return;
    }

    MosbySavedState savedState = (MosbySavedState) state;
    restoreSavedState(savedState);
    delegateCallback.superOnRestoreInstanceState(savedState.getSuperState());
  }
}
