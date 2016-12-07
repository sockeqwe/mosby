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

package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import com.hannesdorfmann.mosby3.sample.mail.model.account.Account;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AccountManager;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AuthCredentials;
import rx.Observable;

/**
 * @author Hannes Dorfmann
 */
public class TestAccountManager implements AccountManager {

  @Override public Observable<Account> doLogin(AuthCredentials credentials) {
    return null;
  }

  @Override public Account getCurrentAccount() {
    return null;
  }

  @Override public boolean isUserAuthenticated() {
    return true;
  }
}
