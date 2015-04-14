package com.hannesdorfmann.mosby.sample.mail.label;

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
public interface LabelLayoutComponent {

  LabelPresenter presenter();
}
