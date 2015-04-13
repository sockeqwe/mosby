package com.hannesdorfmann.mosby.mvp.viewstate.layout;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.layout.MvpFrameLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateManager;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewStateSupport;

/**
 * @author Hannes Dorfmann
 */
public abstract class MvpLceFrameLayout<M, V extends MvpView, P extends MvpPresenter<V>> extends MvpFrameLayout<P> implements
    ViewStateSupport<V> {


  protected ViewStateManager<?> viewStateManager = new ViewStateManager<>(this, this);
  protected RestoreableParcelableViewState<V> viewState;
  private boolean restoringViewState = false;

  public MvpLceFrameLayout(Context context) {
    super(context);
  }

  public MvpLceFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MvpLceFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MvpLceFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);

  }


  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // can be overridden in subclasses if needed
  }

  @Override public void setRestoringViewState(boolean retstoringViewState) {
    this.restoringViewState = retstoringViewState;
  }

  @Override public void setViewState(ViewState<V> viewState) {
      this.viewState = (RestoreableParcelableViewState<V>) viewState;
  }

  @Override public ViewState<V> getViewState() {
    return viewState;
  }


  @Override public boolean isRestoringViewState() {
    return restoringViewState;
  }


  @Override public abstract RestoreableParcelableViewState<V> createViewState();



}
