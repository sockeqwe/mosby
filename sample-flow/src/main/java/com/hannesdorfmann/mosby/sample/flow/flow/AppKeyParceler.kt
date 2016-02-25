package com.hannesdorfmann.mosby.sample.flow.flow

import android.os.Parcelable
import flow.KeyParceler

/**
 *
 *
 * @author Hannes Dorfmann
 */
class AppKeyParceler : KeyParceler {

  override fun toParcelable(key: Any?): Parcelable = key as Parcelable

  override fun toKey(parcelable: Parcelable) = parcelable
}