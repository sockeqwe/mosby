package com.hannesdorfmann.mosby3.sample.mvp.model.custom;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */

@ParcelablePlease public class B implements Parcelable {
  String foo;

  public B(String foo) {
    this.foo = foo;
  }
  private B(){

  }

  public String getFoo() {
    return foo;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    BParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<B> CREATOR = new Creator<B>() {
    public B createFromParcel(Parcel source) {
      B target = new B();
      BParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public B[] newArray(int size) {
      return new B[size];
    }
  };
}
