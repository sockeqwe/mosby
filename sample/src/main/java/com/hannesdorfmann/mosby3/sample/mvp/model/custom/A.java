package com.hannesdorfmann.mosby3.sample.mvp.model.custom;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class A implements Parcelable {
  String name;

  public A(String name) {
    this.name = name;
  }
  private A(){

  }

  public String getName() {
    return name;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    AParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<A> CREATOR = new Creator<A>() {
    public A createFromParcel(Parcel source) {
      A target = new A();
      AParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public A[] newArray(int size) {
      return new A[size];
    }
  };
}
