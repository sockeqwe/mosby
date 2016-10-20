package com.hannesdorfmann.mosby.sample.mvp.sce;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;

/**
 * Created by leonardo on 19/10/16.
 */

public interface CountryFormPresenter extends MvpPresenter<CountryFormView<Country>> {
    void saveCountry(Country country);
}
