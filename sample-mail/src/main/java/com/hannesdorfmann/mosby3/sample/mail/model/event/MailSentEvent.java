package com.hannesdorfmann.mosby3.sample.mail.model.event;

import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;

/**
 * Thrown to inform that a mail has been sent successfully
 * @author Hannes Dorfmann
 */
public class MailSentEvent {

  private Mail mail;

  public MailSentEvent(Mail mail) {
    this.mail = mail;
  }

  public Mail getMail() {
    return mail;
  }
}

