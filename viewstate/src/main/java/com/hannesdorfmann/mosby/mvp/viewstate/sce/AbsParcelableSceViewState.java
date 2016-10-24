package com.hannesdorfmann.mosby.mvp.viewstate.sce;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.sce.MvpSceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * Created by leonardo on 24/10/16.
 */

public abstract class AbsParcelableSceViewState<D, V extends MvpSceView<D>>
    extends AbsSceViewState<D, V> implements ParcelableSceViewState<D,V> {

    private static final String KEY_BUNDLE_VIEW_STATE =
            "com.hannesdorfmann.mosby.mvp.viewstate.ViewState.bundlekey";

    @Override
    public void saveInstanceState(@NonNull Bundle out) {
        out.putParcelable(KEY_BUNDLE_VIEW_STATE, this);
    }

    @Override
    public RestorableViewState<V> restoreInstanceState(Bundle in) {
        if(in == null)
            return null;

        return (AbsParcelableSceViewState<D,V>) in.getParcelable(KEY_BUNDLE_VIEW_STATE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(currentViewState);
        parcel.writeSerializable(exception);
    }

    protected void readFromParcel(Parcel in) {
        currentViewState = in.readInt();
        exception = (Throwable) in.readSerializable();
        // content will be read in subclass
    }

}
