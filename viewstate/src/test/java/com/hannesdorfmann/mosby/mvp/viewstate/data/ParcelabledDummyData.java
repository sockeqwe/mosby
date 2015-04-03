package com.hannesdorfmann.mosby.mvp.viewstate.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelabledDummyData implements Parcelable {

  public static final Parcelable.Creator<ParcelabledDummyData> CREATOR =
      new Parcelable.Creator<ParcelabledDummyData>() {
        public ParcelabledDummyData createFromParcel(Parcel in) {
          return new ParcelabledDummyData(in);
        }

        public ParcelabledDummyData[] newArray(int size) {
          return new ParcelabledDummyData[size];
        }
      };

  String a = "foo";
  int b = 1;

  public ParcelabledDummyData() {

  }

  private ParcelabledDummyData(Parcel in) {
    a = in.readString();
    b = in.readInt();
  }



  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(a);
    dest.writeInt(b);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ParcelabledDummyData parcelabledDummyData = (ParcelabledDummyData) o;

    if (b != parcelabledDummyData.b) return false;
    return a.equals(parcelabledDummyData.a);
  }

  @Override public int hashCode() {
    int result = a.hashCode();
    result = 31 * result + b;
    return result;
  }
}