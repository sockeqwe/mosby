package com.hannesdorfmann.mosby3.sample.mail.label;

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface LabelView extends MvpLceView<List<Label>> {

  void showLabel();

  void changeLabel(Mail mail, String label);

  void showChangeLabelFailed(Mail mail, Throwable t);

  void setMail(Mail mail);

}
