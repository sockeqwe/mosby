package com.hannesdorfmann.mosby3.sample.mail.search;

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
    dependencies = MailAppComponent.class) public interface SearchComponent {

  public SearchPresenter presenter();

  public void inject(SearchFragment fragment);
}
