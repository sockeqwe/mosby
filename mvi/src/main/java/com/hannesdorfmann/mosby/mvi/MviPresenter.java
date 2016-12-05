package com.hannesdorfmann.mosby.mvi;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * This type of presenter is responsible to interact with the view in a Model-View-Intent way.
 * A {@link MviPresenter} is the bridge that is repsonsible to setup the reactive flow between view
 * and model
 *
 * @param <V> The type of the view this presenter responds to
 * @param <ViewState> The type of the view state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public class MviPresenter<V extends MvpView, ViewState> implements MvpPresenter<V> {

  @Override public void attachView(V view) {

  }

  @Override public void detachView(boolean retainInstance) {

  }
}
