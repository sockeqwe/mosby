package com.hannesdorfmann.mosby.mvp.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * @author Hannes Dorfmann
 */
public abstract class MvpFrameLayout<P extends MvpPresenter> extends FrameLayout implements
    MvpView{

  protected P presenter;

  public MvpFrameLayout(Context context) {
    super(context);
  }

  public MvpFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MvpFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MvpFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this, this);
  }


  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    presenter = createPresenter();
    if (presenter == null){
      throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
    }
    presenter.attachView(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.detachView(false);
  }

  /**
   * Instantiate a presenter instance
   *
   * @return The {@link MvpPresenter} for this view
   */
  protected abstract P createPresenter();
}
