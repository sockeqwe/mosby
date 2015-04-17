package com.hannesdorfmann.mosby.sample.mail.ui.transition;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.transition.Explode;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21) public class ExplodeFadeExitTransition extends Explode {

  private View senderNameView;
  private View senderMailView;
  private View separatorLine;

  public ExplodeFadeExitTransition(View senderNameView, View senderMailView, View separatorLine) {
    this.senderMailView = senderMailView;
    this.senderNameView = senderNameView;
    this.separatorLine = separatorLine;
    excludeTarget(R.id.toolbar, true);
    excludeTarget(android.R.id.statusBarBackground, true);
    excludeTarget(android.R.id.navigationBarBackground, true);
    excludeTarget(R.id.senderName, true);
    excludeTarget(R.id.senderMail, true);
    excludeTarget(R.id.separatorLine, true);
  }

  @Override public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {

    senderNameView.setVisibility(View.INVISIBLE);
    senderMailView.setVisibility(View.INVISIBLE);
    separatorLine.setVisibility(View.INVISIBLE);
    return super.createAnimator(sceneRoot, startValues, endValues);
  }
}
