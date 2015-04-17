package com.hannesdorfmann.mosby.sample.mail.model.event;

import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;

/**
 * @author Hannes Dorfmann
 */
public class MailSentErrorEvent {

  private Mail mail;
  private Throwable exception;

  public MailSentErrorEvent(Mail mail, Throwable exception) {
    this.mail = mail;
    this.exception = exception;
  }

  public Mail getMail() {
    return mail;
  }

  public Throwable getException() {
    return exception;
  }
}
