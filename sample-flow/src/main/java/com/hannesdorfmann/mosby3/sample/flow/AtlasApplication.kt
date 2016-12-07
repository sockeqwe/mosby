package com.hannesdorfmann.mosby3.sample.flow

import android.app.Application
import android.content.Context
import com.hannesdorfmann.mosby3.sample.flow.dagger.ApplicationComponent
import com.hannesdorfmann.mosby3.sample.flow.dagger.DaggerApplicationComponent

/**
 *
 *
 * @author Hannes Dorfmann
 */
class AtlasApplication : Application() {

  private lateinit var component: ApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = DaggerApplicationComponent.create()
  }

  companion object {
    fun getComponent(context: Context): ApplicationComponent {
      return (context.applicationContext as AtlasApplication).component
    }
  }
}