package com.hannesdorfmann.mosby3.sample.mail.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ExplodeAnimation;
import com.easyandroidanimations.library.FadeInAnimation;
import com.hannesdorfmann.mosby3.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
public class StarView extends ImageView {

  private OnClickListener clickDelegate;
  private boolean starred;

  public StarView(Context context) {
    super(context);
  }

  public StarView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public StarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public void setStarred(boolean starred) {
    this.starred = starred;

    if (starred) {
      setColorFilter(getResources().getColor(R.color.accent));
    } else {
      setColorFilter(getResources().getColor(R.color.star_deactivated));
    }
  }

  @Override public void setOnClickListener(OnClickListener onClickListener) {
    clickDelegate = onClickListener;

    super.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (starred) {
          startRemoveStarAnimation();
        } else {
          startAddStarAnimation();
        }
      }
    });
  }

  private void startRemoveStarAnimation() {
    clearAnimation();
    new ExplodeAnimation(this).setDuration(200).setListener(new AnimationListener() {
      @Override public void onAnimationEnd(Animation animation) {
        setStarred(false);
        new FadeInAnimation(StarView.this).setDuration(100).setListener(new AnimationListener() {
          @Override public void onAnimationEnd(Animation animation) {
            triggerClick();
          }
        }).animate();
      }
    }).animate();
  }

  private void startAddStarAnimation() {
    clearAnimation();
    android.view.animation.Animation animation =
        AnimationUtils.loadAnimation(getContext(), R.anim.pulse);

    animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
      @Override public void onAnimationStart(android.view.animation.Animation animation) {
        setStarred(true);
      }

      @Override public void onAnimationEnd(android.view.animation.Animation animation) {
        triggerClick();
      }

      @Override public void onAnimationRepeat(android.view.animation.Animation animation) {
      }
    });

    startAnimation(animation);
  }

  private void triggerClick() {
    if (clickDelegate != null) {
      clickDelegate.onClick(this);
    }
  }

  // TODO increase hit area
}
