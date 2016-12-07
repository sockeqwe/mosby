package com.hannesdorfmann.mosby3.sample.mail.model.mail.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.hannesdorfmann.mosby3.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailReceivedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * Simulates that a gcm push notification
 *
 * @author Hannes Dorfmann
 */
public class GcmFakeIntentService extends IntentService {

  public static final String KEY_MAIL =
      " com.hannesdorfmann.mosby.sample.mail.model.mail.service.FakeGcm.MAIL";

  @Inject MailProvider mailProvider;
  @Inject EventBus eventBus;
  @Inject IntentStarter intentStarter;

  public GcmFakeIntentService() {
    super("GcmFakeIntentService");
    DaggerServiceComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build()
        .inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {

    Mail mail = intent.getParcelableExtra(KEY_MAIL);

    // simulate network / receiving delay
    try {
      Thread.sleep(3000);
    } catch (Exception e) {
    }

    mail.label(Label.INBOX);
    mailProvider.addMail(mail).subscribe();

    eventBus.post(new MailReceivedEvent(mail));

    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

    Intent startIntent =
        intentStarter.getShowMailInNewActivityIntent(getApplicationContext(), mail);

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startIntent, 0);
    builder.setContentIntent(pendingIntent);

    builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
        mail.getSender().getImageRes()));

    builder.setSmallIcon(R.drawable.ic_launcher)
        .setLights(getResources().getColor(R.color.primary), 1800, 3500)
        .setAutoCancel(true)
        .setContentTitle(mail.getSubject())
        .setContentText(mail.getText())
        .setWhen(mail.getDate().getTime())
        .setVibrate(new long[] { 1000, 1000 });

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    notificationManager.notify(mail.getId(), builder.build());
  }
}
