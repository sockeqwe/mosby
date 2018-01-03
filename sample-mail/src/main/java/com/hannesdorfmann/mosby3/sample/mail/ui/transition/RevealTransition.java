package com.hannesdorfmann.mosby3.sample.mail.ui.transition;

import android.annotation.TargetApi;
import android.transition.Visibility;
import android.animation.Animator;
import android.graphics.Point;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

@TargetApi(21)
public class RevealTransition extends Visibility {
    private final Point mEpicenter;
    private final int mSmallRadius;
    private final int mBigRadius;
    private final long mDuration;

    public RevealTransition(Point epicenter, int smallRadius, int bigRadius, long duration) {
        mEpicenter = epicenter;
        mSmallRadius = smallRadius;
        mBigRadius = bigRadius;
        mDuration = duration;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, mEpicenter.x, mEpicenter.y,
                mSmallRadius, mBigRadius);
        animator.setDuration(mDuration);
        return new PauseableAnimator(animator);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, mEpicenter.x, mEpicenter.y,
                mBigRadius, mSmallRadius);
        animator.setDuration(mDuration);
        return new PauseableAnimator(animator);
    }
}