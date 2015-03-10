package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A ViewState that is parcelable. Activities can only use this kind of ViewState, because saving
 * the ViewState in a bundle as Parcelable during screen orientation changes (from portrait to
 * landscape or vice versa) is the only way to do that for activities
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface ParcelableViewState<V extends MvpView> extends ViewState<V>, Parcelable {

  /**
   * Saves this ViewState to the outgoing bundle.
   * This will typically be called in {@link android.app.Activity#onSaveInstanceState(Bundle)}
   * or in  {@link android.app.Fragment#onSaveInstanceState(Bundle)}
   *
   * @param out The bundle where the viewstate should be stored in
   */
  public void saveInstanceState(Bundle out);

  /**
   * Restores the viewstate that has been saved before with {@link #saveInstanceState(Bundle)}
   *
   * @return true, if the ViewState has been restored successfully by reading the data from the
   * bundle, otherwise false
   */
  public boolean restoreInstanceState(Bundle in);
}
