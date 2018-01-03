/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample.mail.login;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.account.Account;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AccountManager;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AuthCredentials;
import com.hannesdorfmann.mosby3.sample.mail.model.event.LoginSuccessfulEvent;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Hannes Dorfmann
 */
public class LoginPresenter extends MvpBasePresenter<LoginView> {

  private AccountManager accountManager;
  private Subscriber<Account> subscriber;
  private EventBus eventBus;

  @Inject public LoginPresenter(AccountManager accountManager, EventBus eventBus) {
    this.accountManager = accountManager;
    this.eventBus = eventBus;
  }

  public void doLogin(AuthCredentials credentials) {

    if (isViewAttached()) {
      getView().showLoading();
    }

    // Kind of "callback"
    cancelSubscription();
    subscriber = new Subscriber<Account>() {
      @Override public void onCompleted() {
        if (isViewAttached()) {
          getView().loginSuccessful();
        }
      }

      @Override public void onError(Throwable e) {
        if (isViewAttached()) {
          getView().showError();
        }
      }

      @Override public void onNext(Account account) {
        eventBus.post(new LoginSuccessfulEvent(account));
      }
    };

    // do the login
    accountManager.doLogin(credentials)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  /**
   * Cancels any previous callback
   */
  private void cancelSubscription() {
    if (subscriber != null && !subscriber.isUnsubscribed()) {
      subscriber.unsubscribe();
    }
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    if (!retainInstance) {
      cancelSubscription();
    }
  }

  @Override public void attachView(LoginView view) {
    super.attachView(view);
  }
}
