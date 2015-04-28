package com.hannesdorfmann.mosby.sample.mail.profile;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.sample.mail.model.contact.ContactsManager;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby.sample.mail.model.contact.ProfileScreen;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class ProfilePresenter extends MvpLceRxPresenter<ProfileView, List<ProfileScreen>> {

  private ContactsManager contactsManager;

  @Inject public ProfilePresenter(ContactsManager contactsManager) {
    this.contactsManager = contactsManager;
  }

  public void loadScreens(Person person) {
    subscribe(contactsManager.getProfileScreens(person), false);
  }
}
