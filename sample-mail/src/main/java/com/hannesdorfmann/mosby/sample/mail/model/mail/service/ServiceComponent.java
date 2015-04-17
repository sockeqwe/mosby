package com.hannesdorfmann.mosby.sample.mail.model.mail.service;

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
public interface ServiceComponent {

  public void inject(SendMailService service);

  public void inject(GcmFakeIntentService service);
}
