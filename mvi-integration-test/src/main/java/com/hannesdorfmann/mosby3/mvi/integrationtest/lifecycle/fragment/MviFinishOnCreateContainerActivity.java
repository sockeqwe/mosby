package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.hannesdorfmann.mosby3.mvi.integrationtest.R;

public class MviFinishOnCreateContainerActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);

        if (savedInstanceState == null) {
            MviFinishOnCreateFragment f = new MviFinishOnCreateFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commitNow();
        }
    }
}
