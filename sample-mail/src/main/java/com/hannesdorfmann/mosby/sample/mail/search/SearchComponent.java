package com.hannesdorfmann.mosby.sample.mail.search;

import com.hannesdorfmann.mosby.sample.mail.dagger.MailModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton
@Component(
    modules = MailModule.class)
public interface SearchComponent {

  public SearchPresenter presenter();
}
