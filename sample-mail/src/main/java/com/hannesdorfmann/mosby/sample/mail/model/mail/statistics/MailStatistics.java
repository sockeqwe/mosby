package com.hannesdorfmann.mosby.sample.mail.model.mail.statistics;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import java.util.ArrayList;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease
public class MailStatistics implements Parcelable {

  ArrayList<MailsCount> mailsCounts;

  public MailStatistics(ArrayList<MailsCount> mailsCounts) {
    this.mailsCounts = mailsCounts;
  }

  public MailStatistics() {
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    MailStatisticsParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<MailStatistics> CREATOR = new Creator<MailStatistics>() {
    public MailStatistics createFromParcel(Parcel source) {
      MailStatistics target = new MailStatistics();
      MailStatisticsParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public MailStatistics[] newArray(int size) {
      return new MailStatistics[size];
    }
  };

  public ArrayList<MailsCount> getMailsCounts() {
    return mailsCounts;
  }

  public void setMailsCounts(ArrayList<MailsCount> mailsCounts) {
    this.mailsCounts = mailsCounts;
  }
}
