package com.hannesdorfmann.mosby.sample.mvp.sce;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import com.hannesdorfmann.mosby.sample.mvp.model.CountryAsyncSaver;

/**
 * Created by leonardo on 19/10/16.
 */

public class SimpleCountryFormPresenter extends MvpBasePresenter<CountryFormView<Country>> implements CountryFormPresenter {

    private int failingCounter = 0;
    private CountryAsyncSaver countryAsyncSaver;

    @Override
    public void saveCountry(Country country) {
        if(isViewAttached())
            getView().showLoading();

        if(countryAsyncSaver != null && !countryAsyncSaver.isCancelled()){
            countryAsyncSaver.cancel(true);
        }

        countryAsyncSaver = new CountryAsyncSaver(++failingCounter % 2 != 0,
                new CountryAsyncSaver.CountrySaverListener() {
            @Override
            public void onSuccess() {
                if(isViewAttached())
                    getView().showSucceeded();
            }

            @Override
            public void onError(Exception e) {
                if(isViewAttached())
                    getView().showError(e);
            }
        });
        countryAsyncSaver.execute(country);
    }


    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);

        if(!retainInstance){
            if(countryAsyncSaver != null){
                countryAsyncSaver.cancel(true);
            }
        }
    }
}
