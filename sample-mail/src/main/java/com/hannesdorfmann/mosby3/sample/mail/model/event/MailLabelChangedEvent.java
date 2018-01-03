package com.hannesdorfmann.mosby3.sample.mail.model.event;

import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;

/**
 * This event is fired to inform that the label of a mail has changed.
 *
 * @author Hannes Dorfmann
 */
public class MailLabelChangedEvent {

  private Mail mail;
  private String label;

  public MailLabelChangedEvent(Mail mail, String label) {
    this.mail = mail;
    this.label = label;
  }

  public Mail getMail() {
    return mail;
  }

  public String getLabel() {
    return label;
  }
}
