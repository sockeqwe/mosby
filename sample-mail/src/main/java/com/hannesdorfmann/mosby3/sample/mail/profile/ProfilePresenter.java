package com.hannesdorfmann.mosby3.sample.mail.profile;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxLcePresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.ContactsManager;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.ProfileScreen;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class ProfilePresenter extends BaseRxLcePresenter<ProfileView, List<ProfileScreen>> {

  private ContactsManager contactsManager;

  @Inject public ProfilePresenter(ContactsManager contactsManager) {
    this.contactsManager = contactsManager;
  }

  public void loadScreens(Person person) {
    subscribe(contactsManager.getProfileScreens(person), false);
  }
}
