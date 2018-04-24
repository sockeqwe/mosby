package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby3.mvi.MviFragment;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestView;

public class MviFinishOnStartFragment extends MviFragment<LifecycleTestView, LifecycleTestPresenter> implements LifecycleTestView {


    public static LifecycleTestPresenter presenter;
    public static int presenterCreatedCount;

    @NonNull
    @Override
    public LifecycleTestPresenter createPresenter() {
        presenter = new LifecycleTestPresenter();
        presenterCreatedCount++;
        return presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mvi, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().finish();
    }
}
