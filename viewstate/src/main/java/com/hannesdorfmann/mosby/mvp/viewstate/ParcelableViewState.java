package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Parcelable;

/**
 * A ViewState that is parcelable. Activities can only use this kind of ViewState, because saving
 * the ViewState in a bundle as Parcelable during screen orientation changes (from protrait to landscape or vice
 * versa) is the only way to do that for activities
 *
 * @author Hannes Dorfmann
 */
public interface ParcelableViewState<D> extends ViewState<D>, Parcelable {
}
