package com.hannesdorfmann.mosby.sample.mail.ui.event;

import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;

/**
 * Event to inform that a certain
 * @author Hannes Dorfmann
 */
public class ShowMailDetailsEvent {
  private Mail mail;

  public ShowMailDetailsEvent(Mail mail) {
    this.mail = mail;
  }

  public Mail getMail() {
    return mail;
  }
}
