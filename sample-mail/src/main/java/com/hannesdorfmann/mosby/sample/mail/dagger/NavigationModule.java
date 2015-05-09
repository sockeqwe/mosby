package com.hannesdorfmann.mosby.sample.mail.dagger;

import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module public class NavigationModule {

  @Singleton @Provides public IntentStarter providesIntentStarter() {
    return new IntentStarter();
  }
}
