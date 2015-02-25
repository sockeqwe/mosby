package com.hannesdorfmann.mosby;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * A simple activity that uses Butterknife and IcePick
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class MosbyActivity extends ActionBarActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override public void onSupportContentChanged() {
    super.onSupportContentChanged();
    ButterKnife.inject(this);
  }
}
