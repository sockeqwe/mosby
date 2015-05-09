package com.hannesdorfmann.mosby.sample.mail.label;

import com.hannesdorfmann.mosby.sample.mail.dagger.ApplicationWide;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailAppComponent;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import dagger.Component;

/**
 * @author Hannes Dorfmann
 */

@ApplicationWide
@Component(
    modules = MailModule.class,
    dependencies = MailAppComponent.class
)
public interface LabelLayoutComponent {

  LabelPresenter presenter();
}
