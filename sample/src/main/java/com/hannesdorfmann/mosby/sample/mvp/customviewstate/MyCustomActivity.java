package com.hannesdorfmann.mosby.sample.mvp.customviewstate;

import android.os.Bundle;
import android.view.View;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.hannesdorfmann.mosby.sample.R;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomActivity extends MvpViewStateActivity<MyCustomPresenter>
    implements MyCustomView {

  @InjectView(R.id.textViewA) View aView;
  @InjectView(R.id.textViewB) View bView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.my_custom_view);
  }

  @Override public RestoreableViewState createViewState() {
    return new MyCustomViewState();
  }

  @Override public void onNewViewStateInstance() {
    presenter.doA();
  }

  @Override protected MyCustomPresenter createPresenter() {
    return new MyCustomPresenter();
  }

  @Override public void showA() {
    ((MyCustomViewState) viewState).setShowingA(true);
    aView.setVisibility(View.VISIBLE);
    bView.setVisibility(View.GONE);
  }

  @Override public void showB() {
    ((MyCustomViewState) viewState).setShowingA(false);
    aView.setVisibility(View.GONE);
    bView.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.showA) public void onShowAClicked() {
    presenter.doA();
  }

  @OnClick(R.id.showB) public void onShowBClicked() {
    presenter.doB();
  }
}
