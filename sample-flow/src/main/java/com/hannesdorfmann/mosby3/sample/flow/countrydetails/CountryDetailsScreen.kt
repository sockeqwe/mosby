package com.hannesdorfmann.mosby3.sample.flow.countries

import android.os.Parcel
import android.os.Parcelable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountryDetailsScreen(val countryId: Int) : Parcelable {

  private constructor(p: Parcel) : this(p.readInt())

  override fun writeToParcel(parcel: Parcel, p1: Int) {
    parcel.writeInt(countryId)
  }

  override fun describeContents(): Int = 0

  companion object {
    val CREATOR = object : Parcelable.Creator<CountryDetailsScreen> {
      override fun createFromParcel(p: Parcel): CountryDetailsScreen = CountryDetailsScreen(p)

      override fun newArray(size: Int): Array<out CountryDetailsScreen>? = Array(size,
          { CountryDetailsScreen(-1) })
    }
  }
}