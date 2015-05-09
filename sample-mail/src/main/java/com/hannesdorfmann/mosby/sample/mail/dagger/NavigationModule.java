package com.hannesdorfmann.mosby.sample.mail.dagger;

import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Hannes Dorfmann
 */
@Module public class NavigationModule {

  @ApplicationWide @Provides public IntentStarter providesIntentStarter() {
    return new IntentStarter();
  }
}
