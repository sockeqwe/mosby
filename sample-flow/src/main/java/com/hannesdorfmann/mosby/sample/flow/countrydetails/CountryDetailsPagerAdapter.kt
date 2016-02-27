package com.hannesdorfmann.mosby.sample.flow.countrydetails

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby.sample.flow.countries.InfoLayout
import com.hannesdorfmann.mosby.sample.flow.model.DetailsTab

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountryDetailsPagerAdapter(private val context: Context, val tabs: List<DetailsTab>) : PagerAdapter() {

  override fun instantiateItem(container: ViewGroup, position: Int): Any? {
    val layout = InfoLayout(context)
    layout.countryId = tabs[position].countryId
    container.addView(layout)
    return layout
  }

  override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
    container.removeView(o as View)
  }

  override fun isViewFromObject(view: View?, obj: Any?) = view == obj

  override fun getCount() = tabs.size

  override fun getPageTitle(position: Int): CharSequence = context.resources.getString(
      tabs[position].nameRes)
}