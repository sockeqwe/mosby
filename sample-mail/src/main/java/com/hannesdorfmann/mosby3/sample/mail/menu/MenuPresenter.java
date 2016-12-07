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

package com.hannesdorfmann.mosby3.sample.mail.menu;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxAuthPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.MailReadEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import com.hannesdorfmann.mosby3.sample.mail.model.event.LoginSuccessfulEvent;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MenuPresenter extends BaseRxAuthPresenter<MenuView, List<Label>> {

  @Inject public MenuPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadLabels(boolean pullToRefresh) {
    subscribe(mailProvider.getLabels(), pullToRefresh);
  }

  public void onEventMainThread(LoginSuccessfulEvent event) {
    super.onEventMainThread(event);
    if (isViewAttached()){
      getView().setAccount(event.getAccount());
    }
  }

  public void onEventMainThread(MailReadEvent event){
    if (isViewAttached()){
      String label = event.getMail().getLabel();
      getView().decrementUnreadCount(label);
    }
  }

}
