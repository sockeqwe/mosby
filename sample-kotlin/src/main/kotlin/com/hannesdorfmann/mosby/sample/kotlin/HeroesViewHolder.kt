package com.hannesdorfmann.mosby.sample.kotlin

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
/**
 *
 * @author Hannes Dorfmann
 */
class HeroesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var image: ImageView? = null
    var name: TextView? = null

    init {
        image = view.findViewById(R.id.image) as ImageView
        name = view.findViewById(R.id.name) as TextView
    }

}