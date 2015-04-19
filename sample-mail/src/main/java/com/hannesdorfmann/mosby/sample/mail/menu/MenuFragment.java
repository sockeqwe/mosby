/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.mail.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.AuthRefreshRecyclerFragment;
import com.hannesdorfmann.mosby.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby.sample.mail.base.view.viewstate.AuthViewState;
import com.hannesdorfmann.mosby.sample.mail.model.account.Account;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MenuFragment extends AuthRefreshRecyclerFragment<List<Label>, MenuView, MenuPresenter>
    implements MenuView, MenuAdapter.LabelClickListener {

  @InjectView(R.id.email) TextView email;
  @InjectView(R.id.name) TextView name;
  @InjectView(R.id.profilePic) ImageView profilePic;

  @Inject EventBus eventBus;

  private MenuComponent menuComponent;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_menu;
  }

  @Override public AuthViewState<List<Label>, MenuView> createViewState() {
    return new MenuViewState();
  }

  @Override protected ListAdapter<List<Label>> createAdapter() {
    return new MenuAdapter(getActivity(), this);
  }

  @Override public void setAccount(Account account) {

    MenuViewState vs = (MenuViewState) viewState;
    vs.setAccount(account);

    email.setText(account.getEmail());
    name.setText(account.getName());
    profilePic.setImageResource(account.getImageRes());

    email.setVisibility(View.VISIBLE);
    name.setVisibility(View.VISIBLE);
    profilePic.setVisibility(View.VISIBLE);
  }

  @Override public void showAuthenticationRequired() {
    super.showAuthenticationRequired();

    email.setVisibility(View.GONE);
    name.setVisibility(View.GONE);
    profilePic.setVisibility(View.GONE);
  }

  @Override protected MenuPresenter createPresenter() {
    return menuComponent.presenter();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadLabels(pullToRefresh);
  }

  @Override protected void injectDependencies() {
    menuComponent = DaggerMenuComponent.create();
    menuComponent.inject(this);
  }

  @Override public void onLabelClicked(Label label) {
    IntentStarter.showMailsOfLabel(getActivity(), label);
  }

  @Override public void decrementUnreadCount(String label) {

    for (Label l : adapter.getItems()) {
      if (l.getName().equals(label)){
        l.decrementUnreadCount();
        adapter.notifyDataSetChanged();
        return;
      }
    }
  }
}
