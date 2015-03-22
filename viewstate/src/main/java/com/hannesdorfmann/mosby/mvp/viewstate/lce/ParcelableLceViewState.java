package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;

/**
 * A common interface for {@link LceViewState} and {@link ParcelableViewState}.
 * This one is used for {@link MvpLceActivity}
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface ParcelableLceViewState<D, V extends MvpLceView<D>>
    extends ParcelableViewState<V>, LceViewState<D, V> {
}
