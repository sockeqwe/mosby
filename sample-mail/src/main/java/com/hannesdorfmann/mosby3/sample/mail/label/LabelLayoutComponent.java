package com.hannesdorfmann.mosby3.sample.mail.label;

import com.hannesdorfmann.mosby3.sample.mail.dagger.MailAppComponent;
import com.hannesdorfmann.mosby3.sample.mail.dagger.MailModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */

@Singleton
@Component(
    modules = MailModule.class,
    dependencies = MailAppComponent.class
)
public interface LabelLayoutComponent {

  LabelPresenter presenter();
}
