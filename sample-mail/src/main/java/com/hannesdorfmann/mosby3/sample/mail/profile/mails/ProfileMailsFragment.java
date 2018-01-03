package com.hannesdorfmann.mosby3.sample.mail.profile.mails;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseMailsFragment;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;

/**
 * @author Hannes Dorfmann
 */
public class ProfileMailsFragment extends BaseMailsFragment<ProfileMailsView, ProfileMailsPresenter>
    implements ProfileMailsView {

  @Arg Person person;
  ProfileMailsComponent profileMailsComponent;

  @Override public ProfileMailsPresenter createPresenter() {
    return profileMailsComponent.presenter();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadMailsSentBy(person, pullToRefresh);
  }

  @Override protected void injectDependencies() {
    profileMailsComponent = DaggerProfileMailsComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build();

    profileMailsComponent.inject(this);
  }
}
