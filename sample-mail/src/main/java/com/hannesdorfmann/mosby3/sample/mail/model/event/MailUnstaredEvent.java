package com.hannesdorfmann.mosby3.sample.mail.model.event;

/**
 * Fired to inform that an Mail has been starred
 * @author Hannes Dorfmann
 */
public class MailUnstaredEvent {
  private int mailId;

  public MailUnstaredEvent(int mailId) {
    this.mailId = mailId;
  }

  public int getMailId() {
    return mailId;
  }
}
