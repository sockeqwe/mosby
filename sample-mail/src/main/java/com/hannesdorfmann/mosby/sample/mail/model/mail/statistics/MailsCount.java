package com.hannesdorfmann.mosby.sample.mail.model.mail.statistics;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class MailsCount implements Parcelable {

  String label;
  int mailsCount;

  public MailsCount(String label, int mailsCount) {
    this.label = label;
    this.mailsCount = mailsCount;
  }

  private MailsCount() {
  }

  public String getLabel() {
    return label;
  }

  public int getMailsCount() {
    return mailsCount;
  }

  public void incrementCount(){
    mailsCount++;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    MailsCountParcelablePlease.writeToParcel(this, dest, flags);
  }


  public static final Creator<MailsCount> CREATOR = new Creator<MailsCount>() {
    public MailsCount createFromParcel(Parcel source) {
      MailsCount target = new MailsCount();
      MailsCountParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public MailsCount[] newArray(int size) {
      return new MailsCount[size];
    }
  };
}
