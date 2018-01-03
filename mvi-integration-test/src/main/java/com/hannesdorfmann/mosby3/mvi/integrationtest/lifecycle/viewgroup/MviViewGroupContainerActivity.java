package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.viewgroup;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.mvi.integrationtest.R;

/**
 * @author Hannes Dorfmann
 */

public class MviViewGroupContainerActivity extends AppCompatActivity {

  private static Activity currentInstance;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentInstance = this;
    setContentView(R.layout.activity_viewgroup_mvi);
  }

  public static TestMviFrameLayout getMviViewGroup() {
    return (TestMviFrameLayout) currentInstance.findViewById(R.id.testFrameLayout);
  }

  public static void pressBackButton() {
    currentInstance.runOnUiThread(new Runnable() {
      @Override public void run() {
        currentInstance.onBackPressed();
      }
    });
  }

  public static void removeMviViewGroup() {
    currentInstance.runOnUiThread(new Runnable() {
      @Override public void run() {
        ViewGroup rootView = (ViewGroup) currentInstance.findViewById(R.id.rootView);
        rootView.removeAllViews();
      }
    });
  }
}
