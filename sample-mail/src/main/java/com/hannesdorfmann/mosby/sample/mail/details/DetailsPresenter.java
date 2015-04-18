package com.hannesdorfmann.mosby.sample.mail.details;

import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.mosby.sample.mail.base.presenter.RxMailPresenter;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailReadEvent;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class DetailsPresenter extends RxMailPresenter<DetailsView, Mail> {

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
        .compose(new AndroidSchedulerTransformer<Mail>())
        .subscribe();
  }
}
