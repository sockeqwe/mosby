package com.hannesdorfmann.mosby3.sample.mail.ui.transition;

import android.annotation.TargetApi;
import android.transition.Explode;
import com.hannesdorfmann.mosby3.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21)
public class ExcludedExplodeTransition extends Explode {

  public ExcludedExplodeTransition() {
    excludeTarget(R.id.toolbar, true);
    excludeTarget(android.R.id.statusBarBackground, true);
    excludeTarget(android.R.id.navigationBarBackground, true);
  }
}
