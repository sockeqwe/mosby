package com.hannesdorfmann.mosby.mvp.viewstate.lce.data;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * If you really have good reasons you could have <i>Void</i> as content type in a LCE
 * (Loading-Content-Error) View. This is the corresponding {@link LceViewState} and{@link
 * RestoreableViewState}
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @param <V> The type of the view
 * @author Hannes Dorfmann
 * @Since 1.0.0
 */
public class VoidViewState<V extends MvpLceView<Void>> extends AbsParcelableLceViewState<Void, V> {
}
