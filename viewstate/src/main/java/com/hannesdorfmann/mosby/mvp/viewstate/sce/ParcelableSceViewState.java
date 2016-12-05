package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import com.hannesdorfmann.mosby.mvp.sce.MvpSceActivity;
import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;

/**
 * A common interface for {@link SceViewState} and {@link RestorableParcelableViewState}.
 * This one is used for {@link MvpSceActivity}
 *
 * @author Leonardo Ferrari
 * @since 3.0.0
 */
public interface ParcelableSceViewState<D, V extends MvpSceView<D>>
        extends RestorableParcelableViewState<V>, SceViewState<D,V> {
}
