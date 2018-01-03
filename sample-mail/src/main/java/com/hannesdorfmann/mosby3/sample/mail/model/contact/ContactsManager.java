package com.hannesdorfmann.mosby3.sample.mail.model.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func0;

/**
 * @author Hannes Dorfmann
 */
public class ContactsManager {

  public Observable<List<ProfileScreen>> getProfileScreens(Person person) {

    // TODO throw error from time to time
    return Observable.defer(new Func0<Observable<List<ProfileScreen>>>() {
      @Override public Observable<List<ProfileScreen>> call() {

        List<ProfileScreen> screens = new ArrayList<ProfileScreen>();
        screens.add (new ProfileScreen(ProfileScreen.TYPE_MAILS, "Mails"));
        screens.add(new ProfileScreen(ProfileScreen.TYPE_ABOUT, "About"));

        return Observable.just(screens);
      }
    }).delay(2, TimeUnit.SECONDS);
  }
}
