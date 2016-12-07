package com.hannesdorfmann.mosby3.sample.mail.write;

import android.content.Context;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.LoginSuccessfulEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailSentErrorEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailSentEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.NotAuthenticatedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class WritePresenter extends MvpBasePresenter<WriteView> {

  private EventBus eventBus;
  private IntentStarter intentStarter;

  @Inject public WritePresenter(EventBus eventBus, IntentStarter intentStarter) {
    this.eventBus = eventBus;
    this.intentStarter = intentStarter;
  }

  public void writeMail(Context context, Mail mail) {
    getView().showLoading();
    intentStarter.sendMailViaService(context, mail);
  }


  public void onEventMainThread(NotAuthenticatedEvent event) {
    if (isViewAttached()) {
      getView().showAuthenticationRequired();
    }
  }

  public void onEventMainThread(LoginSuccessfulEvent event) {
    if (isViewAttached()) {
      getView().showForm();
    }
  }

  public void onEventMainThread(MailSentErrorEvent errorEvent){
    if (isViewAttached()){
      getView().showError(errorEvent.getException());
    }
  }


  public void onEventMainThread(MailSentEvent event){
    if (isViewAttached()){
      getView().finishBecauseSuccessful();
    }
  }



  @Override public void attachView(WriteView view) {
    super.attachView(view);
    eventBus.register(this);
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    eventBus.unregister(this);
  }
}
