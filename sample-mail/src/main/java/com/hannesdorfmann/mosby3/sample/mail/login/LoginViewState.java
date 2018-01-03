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

import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;

/**
 * @author Hannes Dorfmann
 */
public class LoginViewState implements ViewState<LoginView> {

  final int STATE_SHOW_LOGIN_FORM = 0;
  final int STATE_SHOW_LOADING = 1;
  final int STATE_SHOW_ERROR = 2;

  int state = STATE_SHOW_LOGIN_FORM;

  @Override public void apply(LoginView view, boolean retained) {

    switch (state) {
      case STATE_SHOW_LOADING:
        view.showLoading();
        break;

      case STATE_SHOW_ERROR:
        view.showError();
        break;

      case STATE_SHOW_LOGIN_FORM:
        view.showLoginForm();
        break;
    }
  }

  public void setShowLoginForm() {
    state = STATE_SHOW_LOGIN_FORM;
  }

  public void setShowError() {
    state = STATE_SHOW_ERROR;
  }

  public void setShowLoading() {
    state = STATE_SHOW_LOADING;
  }
}
