package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import java.util.Comparator;

/**
 * A Comparator for sorting mails in reverse order. Useful for binary search.
 *
 * @author Hannes Dorfmann
 */
public class MailComparator implements Comparator<Mail> {


  public static final  MailComparator INSTANCE = new MailComparator();

  private MailComparator(){

  }

  @Override public int compare(Mail lhs, Mail rhs) {
    return rhs.getDate().compareTo(lhs.getDate());
  }
}
