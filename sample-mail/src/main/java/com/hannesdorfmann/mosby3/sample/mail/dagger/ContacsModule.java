package com.hannesdorfmann.mosby3.sample.mail.dagger;

import com.hannesdorfmann.mosby3.sample.mail.model.contact.ContactsManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module public class ContacsModule {

  @Singleton @Provides public ContactsManager contactsManager() {
    return new ContactsManager();
  }
}
