package com.hannesdorfmann.mosby.sample.mail.write;

import android.content.Context;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby.sample.mail.model.event.LoginSuccessfulEvent;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailSentErrorEvent;
import com.hannesdorfmann.mosby.sample.mail.model.event.MailSentEvent;
import com.hannesdorfmann.mosby.sample.mail.model.event.NotAuthenticatedEvent;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class WritePresenter extends MvpBasePresenter<WriteView> {

  private EventBus eventBus;


  @Inject public WritePresenter(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void writeMail(Context context, Mail mail) {
    getView().showLoading();
    IntentStarter.sendMailViaService(context, mail);
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
