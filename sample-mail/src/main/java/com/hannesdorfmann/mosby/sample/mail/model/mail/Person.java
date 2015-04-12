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

package com.hannesdorfmann.mosby.sample.mail.model.mail;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Person implements Parcelable {

  int id;

  String name;

  /**
   * The imageRes profile pic resource
   */
  int imageRes;

  public Person(int id, String name, int imageRes) {
    this.id = id;
    this.name = name;
    this.imageRes = imageRes;
  }

  private Person(){

  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getImageRes() {
    return imageRes;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PersonParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Person> CREATOR = new Creator<Person>() {
    public Person createFromParcel(Parcel source) {
      Person target = new Person();
      PersonParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Person[] newArray(int size) {
      return new Person[size];
    }
  };
}
