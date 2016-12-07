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

package com.hannesdorfmann.mosby3.sample.mail.model.account;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
public class DefaultAccountManager implements AccountManager {

  private Account currentAccount;

  /**
   * Returns the Account observable
   */
  @Override public Observable<Account> doLogin(AuthCredentials credentials) {

    return Observable.just(credentials).flatMap(new Func1<AuthCredentials, Observable<Account>>() {
      @Override public Observable<Account> call(AuthCredentials credentials) {

        try {
          // Simulate network delay
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (credentials.getUsername().equals("ted") && credentials.getPassword().equals("robin")) {
          currentAccount = new Account();
          return Observable.just(currentAccount);
        }

        return Observable.error(new LoginException());
      }
    });
  }

  @Override public Account getCurrentAccount() {
    return currentAccount;
  }

  @Override public boolean isUserAuthenticated() {
    return currentAccount != null;
  }
}
