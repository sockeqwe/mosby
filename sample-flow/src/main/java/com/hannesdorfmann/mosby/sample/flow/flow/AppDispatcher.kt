package com.hannesdorfmann.mosby.sample.flow.flow

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.mosby.sample.flow.R
import com.hannesdorfmann.mosby.sample.flow.countries.CountriesScreen
import com.hannesdorfmann.mosby.sample.flow.countries.CountryDetailsScreen
import flow.Dispatcher
import flow.Traversal
import flow.TraversalCallback

/**
 *
 *
 * @author Hannes Dorfmann
 */
class AppDispatcher(private val activity: Activity) : Dispatcher {


  override fun dispatch(traversal: Traversal, callback: TraversalCallback) {

    val container = activity.findViewById(R.id.container) as ViewGroup
    //TransitionManager.beginDelayedTransition(container)
    val destination = traversal.destination.top<Any>()

    if (traversal.origin != null && container.childCount > 0) {
      //traversal.getState((traversal.origin as History).top()).save(container.getChildAt(0))
      container.removeAllViews()
    }

    val layoutRes = when (destination) {
      is CountriesScreen -> R.layout.screen_countries
      is CountryDetailsScreen -> R.layout.screen_countrydetails
      else -> throw IllegalStateException("Unknown screen $destination")
    }

    val incomingView = LayoutInflater.from(traversal.createContext(destination, activity))
        .inflate(layoutRes, container, false)

    container.addView(incomingView)
    traversal.getState(traversal.destination.top()).restore(incomingView)
    callback.onTraversalCompleted()
  }
}