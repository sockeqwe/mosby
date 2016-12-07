package com.hannesdorfmann.mosby3.sample.flow.model

import android.support.annotation.StringRes

/**
 *
 *
 * @author Hannes Dorfmann
 */
sealed class DetailsTab(val countryId: Int, @StringRes val nameRes: Int) {

  class InfoTab(countryId: Int, @StringRes name: Int) : DetailsTab(countryId, name)
  class MapTab(countryId: Int, @StringRes name: Int) : DetailsTab(countryId, name)
  class ImagesTab(countryId: Int, @StringRes name: Int) : DetailsTab(countryId, name)

}