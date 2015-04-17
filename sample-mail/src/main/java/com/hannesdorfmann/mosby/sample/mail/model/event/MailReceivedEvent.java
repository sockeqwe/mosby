package com.hannesdorfmann.mosby.sample.mail.model.event;

import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;

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
