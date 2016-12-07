package com.hannesdorfmann.mosby3.sample.flow.countries

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.hannesdorfmann.mosby3.sample.flow.R
import com.hannesdorfmann.mosby3.sample.flow.model.Country
import com.squareup.picasso.Picasso

/**
 *
 *
 * @author Hannes Dorfmann
 */

class CountryViewHolder(v: View, private val clickCallback: (Country) -> Unit) : RecyclerView.ViewHolder(
    v) {
  val flag: ImageView by bindView(R.id.flag)
  val name: TextView by bindView(R.id.name)
  lateinit var country: Country

  init {
    itemView.setOnClickListener {
      clickCallback(country)
    }
  }
}

class CountriesAdapter(private val inflater: LayoutInflater, private val clickCallback: (Country) -> Unit) : RecyclerView.Adapter<CountryViewHolder>() {

  var items: List<Country>? = null

  override fun onCreateViewHolder(parent: ViewGroup?,
      p1: Int): CountryViewHolder = CountryViewHolder(
      inflater.inflate(R.layout.item_country, parent, false), clickCallback)

  override fun onBindViewHolder(vh: CountryViewHolder, pos: Int) {
    val country = items!![pos]
    vh.country = country
    vh.name.text = country.name
    Picasso.with(vh.itemView.context).load(country.flagUrl).fit().centerInside().into(vh.flag)
  }

  override fun getItemCount(): Int = if (items == null) 0 else items!!.size
}