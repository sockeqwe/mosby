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

package com.hannesdorfmann.mosby.sample.dagger1.members;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import com.hannesdorfmann.mosby.dagger1.viewstate.lce.Dagger1MvpLceViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.ParcelableLceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.hannesdorfmann.mosby.retrofit.exception.NetworkException;
import com.hannesdorfmann.mosby.sample.dagger1.R;
import com.hannesdorfmann.mosby.sample.dagger1.model.User;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class MembersActivity extends
    Dagger1MvpLceViewStateActivity<SwipeRefreshLayout, List<User>, MembersView, MembersPresenter>
    implements MembersView {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_members);
  }

  @Override public ParcelableLceViewState<List<User>, MembersView> createViewState() {
    return new CastedArrayListLceViewState<>();
  }

  @Override public List<User> getData() {
    return null;
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    if (e instanceof NetworkException) {
      return "Error! Are you connected to the internet?";
    }
    return "An error has occurred";
  }

  @Override protected MembersPresenter createPresenter() {
    return getObjectGraph().get(MembersPresenter.class);
  }

  @Override public void setData(List<User> data) {

  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadSquareMembers(pullToRefresh);
  }
}
