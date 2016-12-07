package com.hannesdorfmann.mosby3.mvi;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;

/**
 * This type of presenter is responsible to interact with the view in a Model-View-Intent way.
 * A {@link MviPresenter} is the bridge that is repsonsible to setup the reactive flow between view
 * and model
 *
 * @param <V> The type of the view this presenter responds to
 * @param <VS> The type of the view state
 * @author Hannes Dorfmann
 * @since 3.0
 */
public interface MviPresenter<V extends MvpView, VS> extends MvpPresenter<V> {

  /**
   * Get the observable of the ViewState
   *
   * @return Observable representing the viewstate
   */
  Observable<VS> getViewStateObservable();
}
