package com.hannesdorfmann.mosby3.sample.mail.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21) public class ExplodeFadeEnterTransition extends ExcludedExplodeTransition {

  final View senderNameView;
  final View senderMailView;
  final View separatorLine;

  public ExplodeFadeEnterTransition(View senderNameView, View senderMailView, View separatorLine) {
    this.senderMailView = senderMailView;
    this.senderNameView = senderNameView;
    this.separatorLine = separatorLine;
    excludeTarget(R.id.senderName, true);
    excludeTarget(R.id.senderMail, true);
    excludeTarget(R.id.separatorLine, true);
  }

  @Override public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {

    Animator animator = super.createAnimator(sceneRoot, startValues, endValues);

    animator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        senderNameView.setVisibility(View.VISIBLE);
        senderMailView.setVisibility(View.VISIBLE);
        separatorLine.setVisibility(View.VISIBLE);

        senderNameView.setAlpha(0);
        senderMailView.setAlpha(0);
        separatorLine.setAlpha(0);
      }

      @Override public void onAnimationEnd(Animator animation) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(senderNameView, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(senderMailView, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(separatorLine, "alpha", 0f, 1f));

        set.setDuration(200).start();
      }
    });
    return animator;
  }
}
