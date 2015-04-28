package com.hannesdorfmann.mosby.sample.mail.profile.mails;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.sample.mail.base.view.BaseMailsFragment;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;

/**
 * @author Hannes Dorfmann
 */
public class ProfileMailsFragment extends BaseMailsFragment<ProfileMailsView, ProfileMailsPresenter>
    implements ProfileMailsView {

  @Arg Person person;

  @Override protected ProfileMailsPresenter createPresenter() {
    return DaggerProfileMailsComponent.builder().build().presenter();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadMailsSentBy(person, pullToRefresh);
  }
}
