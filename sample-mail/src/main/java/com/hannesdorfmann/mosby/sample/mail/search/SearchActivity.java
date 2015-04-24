package com.hannesdorfmann.mosby.sample.mail.search;

import android.os.Bundle;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
public class SearchActivity extends MosbyActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragmentContainer, new SearchFragment())
          .commit();
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

}
