package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import android.os.Parcel;
import android.os.Parcelable;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@ParcelablePlease public class Mail implements Parcelable {

  int id;
  Person sender;
  Person receiver;

  String subject;
  Date date;
  boolean read;
  String text;

  String label;
  boolean starred;


  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    MailParcelablePlease.writeToParcel(this, dest, flags);
  }

  public static final Creator<Mail> CREATOR = new Creator<Mail>() {
    public Mail createFromParcel(Parcel source) {
      Mail target = new Mail();
      MailParcelablePlease.readFromParcel(target, source);
      return target;
    }

    public Mail[] newArray(int size) {
      return new Mail[size];
    }
  };

  public Person getSender() {
    return sender;
  }

  public Person getReceiver() {
    return receiver;
  }

  public String getSubject() {
    return subject;
  }

  public Date getDate() {
    return date;
  }

  public boolean isRead() {
    return read;
  }

  public String getText() {
    return text;
  }

  public String getLabel() {
    return label;
  }

  public Mail sender(Person s){
    this.sender = s;
    return this;
  }

  public Mail receiver(Person r){
    this.receiver = r;
    return this;
  }

  public Mail subject(String s){
    this.subject = s;
    return this;
  }

  public Mail date(Date d){
    this.date = d;
    return this;
  }

  public Mail read(boolean read){
    this.read = read;
    return this;
  }

  public Mail text(String text){
    this.text = text;
    return this;
  }

  public Mail label(String label){
    this.label = label;
    return this;
  }

  public Mail id(int id){
    this.id = id;
    return this;
  }

  public int getId() {
    return id;
  }

  public boolean isStarred() {
    return starred;
  }

  public void setStarred(boolean starred) {
    this.starred = starred;
  }

}
