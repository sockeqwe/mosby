package com.hannesdorfmann.mosby3.sample.mail.model.contact;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class ProfileScreen implements Parcelable {

  public static final int TYPE_MAILS = 0;
  public static final int TYPE_ABOUT = 1;

  int type;
  String name;

  private ProfileScreen() {
  }

  public ProfileScreen(int type, String name) {
    this.type = type;
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    ProfileScreenParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<ProfileScreen> CREATOR = new Creator<ProfileScreen>() {
    public ProfileScreen createFromParcel(Parcel source) {
      ProfileScreen target = new ProfileScreen();
      ProfileScreenParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public ProfileScreen[] newArray(int size) {
      return new ProfileScreen[size];
    }
  };
}
