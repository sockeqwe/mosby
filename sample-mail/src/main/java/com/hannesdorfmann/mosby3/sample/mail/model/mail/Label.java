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

package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Label implements Parcelable {

  public static final String INBOX = "Inbox";

  public static final String TRASH = "Trash";

  public static final String SENT = "Sent";

  public static final String SPAM = "Spam";

  String name;
  int iconRes;
  int unreadCount;

  public Label(String name, int iconRes, int unreadCount) {
    this.name = name;
    this.iconRes = iconRes;
    this.unreadCount = unreadCount;
  }

  private Label(){

  }

  public String getName() {
    return name;
  }

  public int getIconRes() {
    return iconRes;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public void decrementUnreadCount(){
    unreadCount--;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    LabelParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Label> CREATOR = new Creator<Label>() {
    public Label createFromParcel(Parcel source) {
      Label target = new Label();
      LabelParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Label[] newArray(int size) {
      return new Label[size];
    }
  };
}
