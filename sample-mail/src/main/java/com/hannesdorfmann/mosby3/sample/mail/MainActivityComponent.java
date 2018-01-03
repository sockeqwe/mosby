package com.hannesdorfmann.mosby3.sample.mail;

import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton
@Component(
    modules = NavigationModule.class) public interface MainActivityComponent {

  void inject(MainActivity activity);
}
