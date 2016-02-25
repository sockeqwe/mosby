package com.hannesdorfmann.mosby.sample.flow.dagger

import com.hannesdorfmann.mosby.sample.flow.countries.CountriesPresenter
import dagger.Component
import javax.inject.Singleton

/**
 *
 *
 * @author Hannes Dorfmann
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

  fun countriesPresenter(): CountriesPresenter;
}