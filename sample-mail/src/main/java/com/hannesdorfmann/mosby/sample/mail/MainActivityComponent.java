package com.hannesdorfmann.mosby.sample.mail;

import com.hannesdorfmann.mosby.sample.mail.dagger.ApplicationWide;
import com.hannesdorfmann.mosby.sample.mail.dagger.NavigationModule;
import dagger.Component;

/**
 * @author Hannes Dorfmann
 */
@ApplicationWide
@Component(
    modules = NavigationModule.class) public interface MainActivityComponent {

  public void inject(MainActivity activity);
}
