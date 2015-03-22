package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Parcelable;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A {@link RestoreableViewState} that is implements {@link Parcelable} interface so that it can be
 * put directly in a bundle (as parcelable)
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface RestoreableParcelableViewState<V extends MvpView>
    extends RestoreableViewState<V>, Parcelable {
}
