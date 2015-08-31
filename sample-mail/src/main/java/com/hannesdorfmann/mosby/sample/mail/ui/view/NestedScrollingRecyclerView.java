package com.hannesdorfmann.mosby.sample.mail.ui.view;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author Hannes Dorfmann
 */
// TODO to delete
public class NestedScrollingRecyclerView extends RecyclerView implements NestedScrollingChild {

  private final NestedScrollingChildHelper scrollingChildHelper =
      new NestedScrollingChildHelper(this);

  public NestedScrollingRecyclerView(Context context) {
    super(context);
  }

  public NestedScrollingRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedScrollingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setNestedScrollingEnabled(boolean enabled) {
    scrollingChildHelper.setNestedScrollingEnabled(enabled);
  }

  public boolean isNestedScrollingEnabled() {
    return scrollingChildHelper.isNestedScrollingEnabled();
  }

  public boolean startNestedScroll(int axes) {
    return scrollingChildHelper.startNestedScroll(axes);
  }

  public void stopNestedScroll() {
    scrollingChildHelper.stopNestedScroll();
  }

  public boolean hasNestedScrollingParent() {
    return scrollingChildHelper.hasNestedScrollingParent();
  }

  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int[] offsetInWindow) {

    return scrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed, offsetInWindow);
  }

  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return scrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return scrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return scrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }
}
