package com.hannesdorfmann.mosby3.sample.flow.flow

import android.os.Parcelable
import flow.KeyParceler

/**
 *
 *
 * @author Hannes Dorfmann
 */
class AtlasAppKeyParceler : KeyParceler {

  override fun toParcelable(key: Any?): Parcelable = key as Parcelable

  override fun toKey(parcelable: Parcelable) = parcelable
}