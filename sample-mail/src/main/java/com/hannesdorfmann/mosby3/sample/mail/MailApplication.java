package com.hannesdorfmann.mosby3.sample.mail;

import android.app.Application;
import android.content.Context;
import com.hannesdorfmann.mosby3.sample.mail.dagger.DaggerMailAppComponent;
import com.hannesdorfmann.mosby3.sample.mail.dagger.MailAppComponent;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * @author Hannes Dorfmann
 */
public class MailApplication extends Application {

  private RefWatcher refWatcher;

  private static MailAppComponent mailComponent;

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    mailComponent = DaggerMailAppComponent.create();
    refWatcher = LeakCanary.install(this);
  }

  public static RefWatcher getRefWatcher(Context context) {
    MailApplication application = (MailApplication) context.getApplicationContext();
    return application.refWatcher;
  }

  public static MailAppComponent getMailComponents() {
    return mailComponent;
  }
}
