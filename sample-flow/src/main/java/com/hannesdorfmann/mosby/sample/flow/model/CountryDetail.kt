package com.hannesdorfmann.mosby.sample.flow.model

/**
 *
 *
 * @author Hannes Dorfmann
 */
data class CountryDetail(val id: Int, val name: String, val imageUrl: String, val tabs: List<DetailsTab>)