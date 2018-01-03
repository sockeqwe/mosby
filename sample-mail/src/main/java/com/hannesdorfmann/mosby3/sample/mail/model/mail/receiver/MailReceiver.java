package com.hannesdorfmann.mosby3.sample.mail.model.mail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.service.GcmFakeIntentService;

/**
 * @author Hannes Dorfmann
 */
public class MailReceiver extends BroadcastReceiver {

  public static final String ACTION_RECEIVE = "com.hannesdorfmann.mosby.sample.mail.RECEIVE";
  public static final String EXTRA_MAIL = "com.hannesdorfmann.mosby.sample.mail.MAIL_DATA";

  @Override public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(ACTION_RECEIVE)) {
      Mail mail = intent.getParcelableExtra(EXTRA_MAIL);

      Intent gcmIntent = new Intent(context, GcmFakeIntentService.class);
      gcmIntent.putExtra(GcmFakeIntentService.KEY_MAIL, mail);
      context.startService(gcmIntent);
    }
  }
}
