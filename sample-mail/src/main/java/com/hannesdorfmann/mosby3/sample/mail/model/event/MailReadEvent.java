package com.hannesdorfmann.mosby3.sample.mail.model.event;

import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;

/**
 * Fired to inform that a certain mail should be marked as read
 *
 * @author Hannes Dorfmann
 */
public class MailReadEvent {

  boolean read;
  Mail mail;

  public MailReadEvent(Mail mail, boolean read) {
    this.mail = mail;
  }

  public Mail getMail() {
    return mail;
  }

  public boolean isRead() {
    return read;
  }
}
