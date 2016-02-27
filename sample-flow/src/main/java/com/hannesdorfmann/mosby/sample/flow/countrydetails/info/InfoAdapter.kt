package com.hannesdorfmann.mosby.sample.flow.countries

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.hannesdorfmann.mosby.sample.flow.R
import com.hannesdorfmann.mosby.sample.flow.model.InfoText

/**
 *
 *
 * @author Hannes Dorfmann
 */

class InfoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
  val title: TextView by bindView(R.id.title)
  val text: TextView by bindView(R.id.text)
}

class InfoAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<InfoViewHolder>() {

  var items: List<InfoText>? = null

  override fun onCreateViewHolder(parent: ViewGroup?,
      p1: Int): InfoViewHolder = InfoViewHolder(
      inflater.inflate(R.layout.item_info, parent, false))

  override fun onBindViewHolder(vh: InfoViewHolder, pos: Int) {
    val info = items!![pos]
    vh.title.setText(info.titleRes)
    vh.text.text = info.text
  }

  override fun getItemCount(): Int = if (items == null) 0 else items!!.size
}