package com.hannesdorfmann.mosby3.sample.flow.countries

import android.os.Parcel
import android.os.Parcelable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountriesScreen : Parcelable {

  override fun writeToParcel(p0: Parcel?, p1: Int) {
  }

  override fun describeContents(): Int = 0

  companion object {
    val CREATOR = object : Parcelable.Creator<CountriesScreen> {
      override fun createFromParcel(p0: Parcel?): CountriesScreen = CountriesScreen()

      override fun newArray(size: Int): Array<out CountriesScreen>? = Array(size,
          { CountriesScreen() })
    }
  }
}