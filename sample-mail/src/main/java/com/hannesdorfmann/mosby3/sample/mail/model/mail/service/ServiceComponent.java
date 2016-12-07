package com.hannesdorfmann.mosby3.sample.mail.model.mail.service;

import com.hannesdorfmann.mosby3.sample.mail.dagger.MailAppComponent;
import com.hannesdorfmann.mosby3.sample.mail.dagger.MailModule;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton @Component(
    modules = { MailModule.class, NavigationModule.class },
    dependencies = MailAppComponent.class)
public interface ServiceComponent {

  public void inject(SendMailService service);

  public void inject(GcmFakeIntentService service);
}
