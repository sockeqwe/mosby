/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.dagger1.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Represents a github user
 *
 * @author Hannes Dorfmann
 */
@ParcelablePlease
public class User implements Parcelable {


  long id;
  String login;
  String avatar_url;

  public long getId() {
    return id;
  }

  public String getLogin() {
    return login;
  }

  public String getAvatarUrl() {
    return avatar_url;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    UserParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    public User createFromParcel(Parcel source) {
      User target = new User();
      UserParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public User[] newArray(int size) {
      return new User[size];
    }
  };
}
