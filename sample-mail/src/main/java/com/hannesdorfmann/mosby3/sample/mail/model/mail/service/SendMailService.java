package com.hannesdorfmann.mosby3.sample.mail.model.mail.service;

import android.app.IntentService;
import android.content.Intent;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AccountManager;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailSentErrorEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailSentEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailGenerator;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.receiver.MailReceiver;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class SendMailService extends IntentService {

  public static final String KEY_MAIL =
      "com.hannesdorfmann.mosby.sample.mail.model.mail.service.MailingService.MAIL";

  @Inject MailProvider mailProvider;
  @Inject EventBus eventBus;
  @Inject AccountManager accountManager;
  @Inject MailGenerator generator;

  public SendMailService() {
    super("MailingService");
    DaggerServiceComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build()
        .inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    final Mail mail = intent.getParcelableExtra(KEY_MAIL);
    mail.label(Label.SENT);
    mailProvider.addMailWithDelay(mail).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(Throwable e) {
        eventBus.post(new MailSentErrorEvent(mail, e));
      }

      @Override public void onNext(Mail mail) {
        eventBus.post(new MailSentEvent(mail));
        generateResponse(mail);
      }
    });
  }

  private void generateResponse(Mail mail) {
    Mail response = generator.generateResponseMail(mail.getReceiver().getEmail());

    if (response != null) {
      response.subject("RE: " + mail.getSubject());
      Intent gcmIntent = new Intent();
      gcmIntent.setAction(MailReceiver.ACTION_RECEIVE);
      gcmIntent.putExtra(MailReceiver.EXTRA_MAIL, response);
      sendBroadcast(gcmIntent);
    }
  }
}
