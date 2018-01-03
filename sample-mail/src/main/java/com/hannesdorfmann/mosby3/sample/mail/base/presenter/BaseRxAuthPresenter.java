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

package com.hannesdorfmann.mosby3.sample.mail.base.presenter;

import com.hannesdorfmann.mosby3.sample.mail.base.view.AuthView;
import com.hannesdorfmann.mosby3.sample.mail.model.account.NotAuthenticatedException;
import com.hannesdorfmann.mosby3.sample.mail.model.event.LoginSuccessfulEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.NotAuthenticatedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;

/**
 * @author Hannes Dorfmann
 */
public class BaseRxAuthPresenter<V extends AuthView<M>, M> extends BaseRxLcePresenter<V, M> {

  protected EventBus eventBus;
  protected MailProvider mailProvider;

  public BaseRxAuthPresenter(MailProvider mailProvider, EventBus eventBus) {
    this.eventBus = eventBus;
    this.mailProvider = mailProvider;
  }

  @Override protected void onError(Throwable e, boolean pullToRefresh) {
    if (e instanceof NotAuthenticatedException) {
      eventBus.post(new NotAuthenticatedEvent());
    } else {
      super.onError(e, pullToRefresh);
    }
  }

  public void onEventMainThread(NotAuthenticatedEvent event) {
    if (isViewAttached()) {
      getView().showAuthenticationRequired();
    }
  }

  public void onEventMainThread(LoginSuccessfulEvent event) {
    if (isViewAttached()) {
      getView().loadData(false);
    }
  }

  @Override public void attachView(V view) {
    super.attachView(view);
    eventBus.register(this);
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    eventBus.unregister(this);
  }
}
