package com.hannesdorfmann.mosby.sample.mvp.sce.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hannesdorfmann.mosby.mvp.sce.MvpSceActivity;
import com.hannesdorfmann.mosby.sample.R;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import com.hannesdorfmann.mosby.sample.mvp.sce.CountryFormPresenter;
import com.hannesdorfmann.mosby.sample.mvp.sce.CountryFormView;
import com.hannesdorfmann.mosby.sample.mvp.sce.SimpleCountryFormPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by leonardo on 19/10/16.
 */

public class CountryFormActivity extends MvpSceActivity<FrameLayout, Country ,CountryFormView<Country>, CountryFormPresenter>
implements CountryFormView<Country> {

    @Bind(R.id.country_form_name)
    EditText countryNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_form);
        ButterKnife.bind(this);
        showForm();
    }

    @OnClick(R.id.country_form_save)
    public void onSaveClick(){
        sendForm(new Country(countryNameEditText.getText().toString()));
    }

    @Override
    protected String getErrorMessage(Throwable e) {
        return null;
    }

    @NonNull
    @Override
    public CountryFormPresenter createPresenter() {
        return new SimpleCountryFormPresenter();
    }

    @Override
    public void sendForm(Country data) {
        presenter.saveCountry(data);
    }
}
