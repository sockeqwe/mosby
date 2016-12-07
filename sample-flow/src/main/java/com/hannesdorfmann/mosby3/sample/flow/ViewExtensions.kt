package com.hannesdorfmann.mosby3.sample.flow

import android.util.DisplayMetrics
import android.view.View

/**
 *
 *
 * @author Hannes Dorfmann
 */
fun View.dpToPx(dp: Float): Float {
  val metrics = context.resources.displayMetrics;
  val px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
  return px
}