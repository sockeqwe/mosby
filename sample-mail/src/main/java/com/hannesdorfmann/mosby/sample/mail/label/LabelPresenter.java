package com.hannesdorfmann.mosby.sample.mail.label;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.mvp.rx.lce.scheduler.SchedulerTransformer;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailLabelChangedEvent;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class LabelPresenter extends MvpLceRxPresenter<LabelView, List<Label>> {

  private EventBus eventBus;
  private MailProvider mailProvider;
  private SchedulerTransformer schedulerTransformer;

  @Inject public LabelPresenter(EventBus eventBus, MailProvider mailProvider,
      SchedulerTransformer transformer) {
    this.eventBus = eventBus;
    this.mailProvider = mailProvider;
    this.schedulerTransformer = transformer;
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

    mailProvider.setLabel(mail.getId(), newLabel)
        .compose(schedulerTransformer)
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
