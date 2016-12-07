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

import android.os.Parcel;
import com.hannesdorfmann.mosby3.sample.mail.base.view.viewstate.AuthCastedArrayListViewState;
import com.hannesdorfmann.mosby3.sample.mail.model.account.Account;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@SuppressWarnings("ParcelCreator")
public class MenuViewState extends AuthCastedArrayListViewState<List<Label>, MenuView> {

  private Account account;

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(account, flags);
  }

  @Override protected void readFromParcel(Parcel source) {
    super.readFromParcel(source);
    account = source.readParcelable(Account.class.getClassLoader());
  }

  @Override public void apply(MenuView view, boolean retained) {
    super.apply(view, retained);

    if (account != null) {
      view.setAccount(account);
    } else {
      view.showAuthenticationRequired();
    }
  }

  @Override public void setShowingAuthenticationRequired() {
    super.setShowingAuthenticationRequired();
    account = null;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
