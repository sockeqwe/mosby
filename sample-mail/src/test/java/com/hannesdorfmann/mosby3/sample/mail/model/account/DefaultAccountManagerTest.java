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

import junit.framework.Assert;
import org.junit.Test;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class DefaultAccountManagerTest {

  @Test public void loginSuccessful() {
    AccountManager manager = new DefaultAccountManager();
    manager.doLogin(new AuthCredentials("ted", "robin")).subscribe(new Subscriber<Account>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(Throwable e) {
        Assert.fail("Login failed, but should be successful");
      }

      @Override public void onNext(Account account) {
        Assert.assertEquals("Ted Mosby", account.getName());
      }
    });
  }

  @Test public void loginFail() {
    AccountManager manager = new DefaultAccountManager();
    manager.doLogin(new AuthCredentials("Foo", "Password")).subscribe(new Subscriber<Account>() {
      @Override public void onCompleted() {
        Assert.fail("Login successful, but should be fail");
      }

      @Override public void onError(Throwable e) {
        Assert.assertTrue(e instanceof LoginException);
      }

      @Override public void onNext(Account account) {
      }
    });
  }
}
