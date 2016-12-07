package com.hannesdorfmann.mosby3.sample.flow.model

import android.support.annotation.StringRes

/**
 *
 *
 * @author Hannes Dorfmann
 */
data class InfoText(@StringRes val titleRes: Int, val text: String) : Info