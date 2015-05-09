package com.hannesdorfmann.mosby.sample.mail.profile;

import com.hannesdorfmann.mosby.sample.mail.dagger.ContacsModule;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailAppComponent;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton @Component(
    modules = {
        MailModule.class, ContacsModule.class,

    },
dependencies = MailAppComponent.class) public interface ProfileComponent {

  public ProfilePresenter presenter();
}
