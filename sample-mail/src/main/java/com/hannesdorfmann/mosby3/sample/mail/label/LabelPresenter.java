package com.hannesdorfmann.mosby3.sample.mail.label;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxLcePresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailLabelChangedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Hannes Dorfmann
 */
public class LabelPresenter extends BaseRxLcePresenter<LabelView, List<Label>> {

  private EventBus eventBus;
  private MailProvider mailProvider;

  @Inject public LabelPresenter(EventBus eventBus, MailProvider mailProvider) {
    this.eventBus = eventBus;
    this.mailProvider = mailProvider;
  }

  @Override public void attachView(LabelView view) {
    super.attachView(view);
    eventBus.register(this);
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    eventBus.unregister(this);
  }

  public void loadLabels() {
    subscribe(mailProvider.getLabels(), false);
  }

  public void setLabel(final Mail mail, String newLabel) {

    // Optimistic propagation
    final String oldLabel = mail.getLabel();
    eventBus.post(new MailLabelChangedEvent(mail, newLabel));

    mailProvider.setLabel(mail, newLabel)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Mail>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            eventBus.post(new MailLabelChangedEvent(mail, oldLabel));
            if (isViewAttached()) {
              getView().changeLabel(mail, oldLabel);
              getView().showChangeLabelFailed(mail, e);
            }
          }

          @Override public void onNext(Mail m) {
          }
        });

    // Don't cancel this onDetach

  }

  public void onEventMainThread(MailLabelChangedEvent e) {
    if (isViewAttached()) {
      getView().changeLabel(e.getMail(), e.getLabel());
    }
  }
}
