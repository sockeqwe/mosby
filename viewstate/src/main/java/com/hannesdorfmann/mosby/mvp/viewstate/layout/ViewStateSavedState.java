package com.hannesdorfmann.mosby.mvp.viewstate.layout;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableParcelableViewState;

/**
 * The SavedState implementation to store the view state in layouts
 *
 * @author Hannes Dorfmann
 * @since 1.1
 */
public class ViewStateSavedState extends View.BaseSavedState {

  public static final Parcelable.Creator<ViewStateSavedState> CREATOR =
      new Parcelable.Creator<ViewStateSavedState>() {
        public ViewStateSavedState createFromParcel(Parcel in) {
          return new ViewStateSavedState(in);
        }

        public ViewStateSavedState[] newArray(int size) {
          return new ViewStateSavedState[size];
        }
      };

  private RestoreableParcelableViewState mosbyViewState;

  public ViewStateSavedState(Parcelable superState) {
    super(superState);
  }

  private ViewStateSavedState(Parcel in) {
    super(in);
    this.mosbyViewState = in.readParcelable(RestoreableParcelableViewState.class.getClassLoader());
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeParcelable(mosbyViewState, flags);
  }

  public RestoreableParcelableViewState getMosbyViewState() {
    return mosbyViewState;
  }

  public void setMosbyViewState(RestoreableParcelableViewState mosbyViewState) {
    this.mosbyViewState = mosbyViewState;
  }
}
