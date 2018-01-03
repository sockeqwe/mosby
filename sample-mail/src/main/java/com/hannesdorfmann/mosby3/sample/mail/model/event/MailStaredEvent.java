package com.hannesdorfmann.mosby3.sample.mail.model.event;

/**
 * Fired to inform that an Mail has been starred
 * @author Hannes Dorfmann
 */
public class MailStaredEvent {
  private int mailId;

  public MailStaredEvent(int mailId) {
    this.mailId = mailId;
  }

  public int getMailId() {
    return mailId;
  }
}
