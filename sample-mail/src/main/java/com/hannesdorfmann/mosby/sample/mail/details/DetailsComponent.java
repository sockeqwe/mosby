package com.hannesdorfmann.mosby.sample.mail.details;

import com.hannesdorfmann.mosby.sample.mail.dagger.MailAppComponent;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import com.hannesdorfmann.mosby.sample.mail.dagger.NavigationModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton
@Component(
    modules = { MailModule.class, NavigationModule.class },
    dependencies = MailAppComponent.class
)
public interface DetailsComponent {

  public DetailsPresenter presenter();

  public void inject(DetailsFragment fragment);

}
