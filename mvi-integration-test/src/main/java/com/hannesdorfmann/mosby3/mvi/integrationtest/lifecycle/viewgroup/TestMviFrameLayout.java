package com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestPresenter;
import com.hannesdorfmann.mosby3.mvi.integrationtest.lifecycle.LifecycleTestView;
import com.hannesdorfmann.mosby3.mvi.layout.MviFrameLayout;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author Hannes Dorfmann
 */

public class TestMviFrameLayout extends MviFrameLayout<LifecycleTestView, LifecycleTestPresenter> implements  LifecycleTestView{

  public final LifecycleTestPresenter presenter = new LifecycleTestPresenter();
  public int createPresenterInvocations = 0;

  public TestMviFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public LifecycleTestPresenter createPresenter() {
    createPresenterInvocations++;
    return presenter;
  }
}
