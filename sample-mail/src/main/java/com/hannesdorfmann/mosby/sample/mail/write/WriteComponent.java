package com.hannesdorfmann.mosby.sample.mail.write;

import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton
@Component(
    modules = MailModule.class)
public interface WriteComponent {

  public WritePresenter presenter();
}
