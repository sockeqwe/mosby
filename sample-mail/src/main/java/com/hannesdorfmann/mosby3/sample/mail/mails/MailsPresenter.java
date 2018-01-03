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

package com.hannesdorfmann.mosby3.sample.mail.mails;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailLabelChangedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailReceivedEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailSentEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MailsPresenter extends BaseRxMailPresenter<MailsView, List<Mail>> {

  @Inject public MailsPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void load(boolean pullToRefresh, Label label) {
    subscribe(mailProvider.getMailsOfLabel(label.getName()), pullToRefresh);
  }

  public void onEventMainThread(MailSentEvent event) {
    onEventMainThread(new MailLabelChangedEvent(event.getMail(), event.getMail().getLabel()));
  }

  public void onEventMainThread(MailReceivedEvent event) {
    onEventMainThread(new MailLabelChangedEvent(event.getMail(), event.getMail().getLabel()));
  }

  public void onEventMainThread(MailLabelChangedEvent event) {
    if (isViewAttached()) {
      getView().changeLabel(event.getMail(), event.getLabel());
    }
  }
}
