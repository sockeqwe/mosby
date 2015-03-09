package com.hannesdorfmann.mosby.mvp.viewstate.lce;

import com.hannesdorfmann.mosby.mvp.viewstate.ParcelableViewState;

/**
 * If you really have good reasons you could have <i>Void</i> as content type in a LCE
 * (Loading-Content-Error) View. This is the corresponding {@link LceViewState} and{@link
 * ParcelableViewState}
 * <p>
 * Can be used for Activites and Fragments.
 * </p>
 *
 * @author Hannes Dorfmann
 * @Since 1.0.0
 */
public class VoidViewState extends AbsLceViewState<Void> {
}
