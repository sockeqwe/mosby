package com.hannesdorfmann.mosby.sample.mail;

import android.app.Application;
import com.hannesdorfmann.mosby.sample.mail.dagger.DaggerMailAppComponent;
import com.hannesdorfmann.mosby.sample.mail.dagger.MailAppComponent;

/**
 * @author Hannes Dorfmann
 */
public class MailApplication extends Application {

  private static MailAppComponent mailComponent;

  @Override public void onCreate() {
    super.onCreate();
    mailComponent = DaggerMailAppComponent.create();
  }

  public static MailAppComponent getMailComponents() {
    return mailComponent;
  }
}
