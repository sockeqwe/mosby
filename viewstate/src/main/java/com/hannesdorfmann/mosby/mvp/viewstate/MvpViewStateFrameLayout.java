package com.hannesdorfmann.mosby.mvp.viewstate;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author Hannes Dorfmann
 */
public abstract class MvpViewStateFrameLayout extends FrameLayout {

  public MvpViewStateFrameLayout(Context context) {
    super(context);
  }

  public MvpViewStateFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MvpViewStateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MvpViewStateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

  }
}
