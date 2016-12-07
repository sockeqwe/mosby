package com.hannesdorfmann.mosby3.sample.flow.countries

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.hannesdorfmann.mosby3.sample.flow.R
import com.hannesdorfmann.mosby3.sample.flow.model.Info
import com.hannesdorfmann.mosby3.sample.flow.model.InfoPicture
import com.hannesdorfmann.mosby3.sample.flow.model.InfoText
import com.squareup.picasso.Picasso

/**
 *
 *
 * @author Hannes Dorfmann
 */

class InfoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
  val title: TextView by bindView(R.id.title)
  val text: TextView by bindView(R.id.text)
  override fun toString(): String {
    return "InfoViewHolder " + super.toString()
  }
}

class InfoPictureViewHolder(v: View) : RecyclerView.ViewHolder(v) {
  val image: ImageView by bindView(R.id.image)

  override fun toString(): String {
    return "InfoPictureViewHolder " + super.toString()
  }
}

class InfoAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val VIEWTYPE_TEXT = 0
  private val VIEWTYPE_IMAGE = 1

  var items: List<Info>? = null

  override fun getItemViewType(position: Int): Int {
    val item = items!![position]
    return when (item) {
      is InfoText -> VIEWTYPE_TEXT
      is InfoPicture -> VIEWTYPE_IMAGE
      else -> throw Exception("unknown viewholder")
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
      when (viewType) {
        VIEWTYPE_TEXT -> InfoViewHolder(
            inflater.inflate(R.layout.item_info, parent, false))

        VIEWTYPE_IMAGE -> InfoPictureViewHolder(
            inflater.inflate(R.layout.item_picture, parent, false))

        else -> throw Exception("unknown viewholder")
      }

  override fun onBindViewHolder(vh: RecyclerView.ViewHolder, pos: Int) {
    when (vh) {
      is InfoViewHolder -> {
        val info = items!![pos] as InfoText
        vh.title.setText(info.titleRes)
        vh.text.text = info.text
      }


      is InfoPictureViewHolder -> {
        val info = items!![pos] as InfoPicture
        Picasso.with(vh.itemView.context)
            .load(info.url)
            .placeholder(R.color.image_placeholder)
            .fit()
            .centerInside()
            .into(vh.image)
      }

      else -> throw Exception("unknown viewholder")
    }
  }

  override fun getItemCount(): Int = if (items == null) 0 else items!!.size
}
