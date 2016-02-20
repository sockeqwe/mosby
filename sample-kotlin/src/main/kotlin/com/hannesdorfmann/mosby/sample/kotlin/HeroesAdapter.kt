package com.hannesdorfmann.mosby.sample.kotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.mosby.sample.kotlin.model.Hero
import com.squareup.picasso.Picasso

/**
 *
 *
 * @author Hannes Dorfmann
 */
class HeroesAdapter(val context: Context, val inflater: LayoutInflater) : RecyclerView.Adapter<HeroesViewHolder> () {

    var items: List<Hero>? = null

    override fun getItemCount(): Int {
        return when (items) {
            null -> 0
            else -> items!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HeroesViewHolder? {
        return HeroesViewHolder(inflater.inflate(R.layout.list_hero_item, parent, false))
    }

    override fun onBindViewHolder(holder: HeroesViewHolder, position: Int) {
        val hero = items!!.get(position)

        Picasso.with(context)
                .load(hero.imageUrl)
                .placeholder(R.color.loading_placeholder)
                .error(R.color.loading_placeholder)
                .into(holder.image)

        holder.name?.text = hero.name
    }
}