package com.hannesdorfmann.mosby3.sample.mail.details;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailReadEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Hannes Dorfmann
 */
public class DetailsPresenter extends BaseRxMailPresenter<DetailsView, Mail> {

  @Inject public DetailsPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadMail(int id) {
    subscribe(mailProvider.getMail(id), false);
  }

  @Override protected void onNext(Mail data) {
    super.onNext(data);
    if (isViewAttached() && !data.isRead()) {
      markAsRead(data);
    }
  }

  private void markAsRead(final Mail mail) {

    // We assume that this call could never fail
    eventBus.post(new MailReadEvent(mail, true));
    mailProvider.markAsRead(mail, true)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }
}
