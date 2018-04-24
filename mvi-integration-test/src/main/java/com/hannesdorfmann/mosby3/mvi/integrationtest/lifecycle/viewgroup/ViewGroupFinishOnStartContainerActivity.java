package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.viewgroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hannesdorfmann.mosby3.mvi.integrationtest.R;

public class ViewGroupFinishOnStartContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_finish_on_create_container);
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
    }

    public ViewGroupFinishOnCreateLayout getLayout() {
        return (ViewGroupFinishOnCreateLayout) findViewById(R.id.mviLayout);
    }
}
