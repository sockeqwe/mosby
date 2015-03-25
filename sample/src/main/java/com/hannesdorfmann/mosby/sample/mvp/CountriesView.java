package com.hannesdorfmann.mosby.sample.mvp;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import java.util.List;

/**
 * The View interface. It's not really needed to do it this way (defining a own interface).
 * We could also use Mvp MvpLceView<List<Country>>
 * directly instead.
 *
 * @author Hannes Dorfmann
 */
public interface CountriesView extends MvpLceView<List<Country>> {
}
