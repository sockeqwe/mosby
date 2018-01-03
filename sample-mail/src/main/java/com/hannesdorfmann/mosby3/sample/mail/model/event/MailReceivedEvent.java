package com.hannesdorfmann.mosby3.sample.mail.model.event;

import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;

/**
 * @author Hannes Dorfmann
 */
public class MailReceivedEvent {

  private Mail mail;

  public MailReceivedEvent(Mail mail) {
    this.mail = mail;
  }

  public Mail getMail() {
    return mail;
  }
}
