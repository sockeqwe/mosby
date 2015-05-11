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

package com.hannesdorfmann.mosby.sample.mail.login;

import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */

public class LoginViewStateTest {

  @Test public void testShowLoginForm() {

    final AtomicBoolean loginCalled = new AtomicBoolean(false);
    LoginView view = new LoginView() {

      @Override public void showLoginForm() {
        loginCalled.set(true);
      }

      @Override public void showError() {
        Assert.fail("showError() instead of showLoginForm()");
      }

      @Override public void showLoading() {
        Assert.fail("showLoading() instead of showLoginForm()");
      }

      @Override public void loginSuccessful() {
        Assert.fail("loginSuccessful() instead of showLoginForm()");
      }
    };

    LoginViewState viewState = new LoginViewState();
    viewState.setShowLoginForm();
    viewState.apply(view, false);
    Assert.assertTrue(loginCalled.get());
  }

  @Test public void testShowError() {

    final AtomicBoolean errorCalled = new AtomicBoolean(false);
    LoginView view = new LoginView() {

      @Override public void showLoginForm() {
        Assert.fail("showLoginForm() instead of showError()");
      }

      @Override public void showError() {
        errorCalled.set(true);
      }

      @Override public void showLoading() {
        Assert.fail("showLoading() instead of showError()");
      }

      @Override public void loginSuccessful() {
        Assert.fail("loginSuccessful() instead of showError()");
      }
    };

    LoginViewState viewState = new LoginViewState();
    viewState.setShowError();
    viewState.apply(view, false);
    Assert.assertTrue(errorCalled.get());
  }

  @Test public void testShowLoading() {

    final AtomicBoolean loadingCalled = new AtomicBoolean(false);
    LoginView view = new LoginView() {

      @Override public void showLoginForm() {
        Assert.fail("showLoginForm() instead of showLoading()");
      }

      @Override public void showError() {
        Assert.fail("showError() instead of showLoading()");
      }

      @Override public void showLoading() {
        loadingCalled.set(true);
      }

      @Override public void loginSuccessful() {
        Assert.fail("loginSuccessful() instead of showLoading()");
      }
    };

    LoginViewState viewState = new LoginViewState();
    viewState.setShowLoading();
    viewState.apply(view, false);
    Assert.assertTrue(loadingCalled.get());
  }
}
