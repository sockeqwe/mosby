/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.mail.model.account;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Account implements Parcelable {

  String name = "Ted Mosby";
  String email = Person.MAIL_TED;
  int imageRes = R.drawable.ted;

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public int getImageRes() {
    return imageRes;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    AccountParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Account> CREATOR = new Creator<Account>() {
    public Account createFromParcel(Parcel source) {
      Account target = new Account();
      AccountParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Account[] newArray(int size) {
      return new Account[size];
    }
  };
}
