package com.hannesdorfmann.mosby.sample.mail.dagger;

import com.hannesdorfmann.mosby.sample.mail.model.contact.ContactsManager;
import dagger.Module;
import dagger.Provides;

/**
 * @author Hannes Dorfmann
 */
@Module public class ContacsModule {

  @ApplicationWide @Provides public ContactsManager contactsManager() {
    return new ContactsManager();
  }
}
