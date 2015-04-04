/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.mvp.customviewstate;

import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.sample.R;
import com.hannesdorfmann.mosby.sample.mvp.model.custom.A;
import com.hannesdorfmann.mosby.sample.mvp.model.custom.B;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomFragment extends MvpViewStateFragment<MyCustomPresenter>
    implements MyCustomView {

  @InjectView(R.id.textViewA) TextView aView;
  @InjectView(R.id.textViewB) TextView bView;

  @Override protected int getLayoutRes() {
    return R.layout.my_custom_view;
  }

  @Override public ViewState createViewState() {
    return new MyCustomViewState();
  }

  @Override public void onNewViewStateInstance() {
    presenter.doA();
  }

  @Override protected MyCustomPresenter createPresenter() {
    return new MyCustomPresenter();
  }

  @Override public void showA(A a) {
    MyCustomViewState vs = ((MyCustomViewState) viewState);
    vs.setShowingA(true);
    vs.setData(a);
    aView.setText(a.getName());
    aView.setVisibility(View.VISIBLE);
    bView.setVisibility(View.GONE);
  }

  @Override public void showB(B b) {
    MyCustomViewState vs = ((MyCustomViewState) viewState);
    vs.setShowingA(false);
    vs.setData(b);
    bView.setText(b.getFoo());
    aView.setVisibility(View.GONE);
    bView.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.loadA) public void onLoadAClicked() {
    presenter.doA();
  }

  @OnClick(R.id.loadB) public void onLoadBClicked() {
    presenter.doB();
  }
}
