package com.hannesdorfmann.mosby3.sample.flow.dagger

import com.hannesdorfmann.mosby3.sample.flow.model.Atlas
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *
 *
 * @author Hannes Dorfmann
 */
@Module
class ApplicationModule {

  @Provides @Singleton
  fun provideAtlas(): Atlas = Atlas()
}