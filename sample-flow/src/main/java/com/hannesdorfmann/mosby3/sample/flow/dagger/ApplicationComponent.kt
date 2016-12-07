package com.hannesdorfmann.mosby3.sample.flow.dagger

import com.hannesdorfmann.mosby3.sample.flow.countries.CountriesPresenter
import com.hannesdorfmann.mosby3.sample.flow.countries.CountryDetailsPresenter
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

  fun countriesPresenter(): CountriesPresenter

  fun countryDetailsPresenter(): CountryDetailsPresenter

}