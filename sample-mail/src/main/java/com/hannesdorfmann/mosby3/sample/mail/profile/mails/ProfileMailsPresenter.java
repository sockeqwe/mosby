package com.hannesdorfmann.mosby3.sample.mail.profile.mails;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class ProfileMailsPresenter extends BaseRxMailPresenter<ProfileMailsView, List<Mail>> {

  @Inject public ProfileMailsPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadMailsSentBy(Person person, boolean pullToRefresh) {

    subscribe(mailProvider.getMailsSentBy(person.getId()), pullToRefresh);
  }
}
