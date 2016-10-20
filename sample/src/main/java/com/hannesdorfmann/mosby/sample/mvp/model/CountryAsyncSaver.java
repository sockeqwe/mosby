package com.hannesdorfmann.mosby.sample.mvp.model;

import android.os.AsyncTask;

/**
 * Created by leonardo on 19/10/16.
 */

public class CountryAsyncSaver extends AsyncTask<Country, Void, Void> {

    public interface CountrySaverListener {
        void onSuccess();
        void onError(Exception e);
    }

    private final boolean shouldFail;
    private final CountrySaverListener listener;

    public CountryAsyncSaver(boolean shouldFail, CountrySaverListener listener) {
        this.shouldFail = shouldFail;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Country... countries) {

        try {
            Thread.sleep(2800);
        }catch (InterruptedException e) {
            return null;
        }

        if(countries.length == 0) {
            listener.onError(new IllegalStateException("No country to add !"));
            return null;
        }

        CountryApi.addCountry(countries[0]);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(isCancelled())
            return;

        if(shouldFail){
            listener.onError(new Exception("Something went wrong :("));
        }else{
            listener.onSuccess();
        }
    }
}
