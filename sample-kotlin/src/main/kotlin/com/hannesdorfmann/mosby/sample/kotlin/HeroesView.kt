package com.hannesdorfmann.mosby.sample.kotlin

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import com.hannesdorfmann.mosby.sample.kotlin.model.Hero

/**
 * MVP View interface
 *
 * @author Hannes Dorfmann
 */
interface HeroesView : MvpLceView<List<Hero>> {

}