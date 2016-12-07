package com.hannesdorfmann.mosby3.sample.mail.label;

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface LabelView extends MvpLceView<List<Label>> {

  public void showLabel();

  public void changeLabel(Mail mail, String label);

  public void showChangeLabelFailed(Mail mail, Throwable t);

  public void setMail(Mail mail);

}
