package com.hannesdorfmann.mosby.sample.mail.dagger;

import com.hannesdorfmann.mosby.sample.mail.model.contact.ContactsManager;
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
