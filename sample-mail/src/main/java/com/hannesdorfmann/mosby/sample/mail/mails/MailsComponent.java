package com.hannesdorfmann.mosby.sample.mail.mails;

import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton
@Component(
    modules = MailModule.class
)
public interface MailsComponent {

  public MailsPresenter presenter();

  public void inject(MailsFragment fragment);
}
