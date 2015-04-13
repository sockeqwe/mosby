package com.hannesdorfmann.mosby.sample.mail.details;

import com.hannesdorfmann.mosby.sample.mail.base.presenter.RxMailPresenter;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class DetailsPresenter extends RxMailPresenter<DetailsView, Mail> {

  @Inject public DetailsPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadMail(int id) {
    subscribe(mailProvider.getMail(id), false);
  }
}
