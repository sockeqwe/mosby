package com.hannesdorfmann.mosby3.sample.mail.statistics;

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
    dependencies = MailAppComponent.class)
public interface StatisticsComponent {

  public StatisticsPresenter presenter();
}
