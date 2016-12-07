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

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseViewStateFragment;
import com.hannesdorfmann.mosby3.sample.mail.model.account.AuthCredentials;
import com.hannesdorfmann.mosby3.sample.mail.utils.KeyboardUtils;
import com.hkm.ui.processbutton.iml.ActionProcessButton;

/**
 * @author Hannes Dorfmann
 */
public class LoginFragment extends BaseViewStateFragment<LoginView, LoginPresenter>
    implements LoginView {

  @Bind(R.id.username) EditText username;
  @Bind(R.id.password) EditText password;
  @Bind(R.id.loginButton) ActionProcessButton loginButton;
  @Bind(R.id.errorView) TextView errorView;
  @Bind(R.id.loginForm) ViewGroup loginForm;

  private LoginComponent loginComponent;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_login;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loginButton.setMode(ActionProcessButton.Mode.ENDLESS);

    int startDelay = getResources().getInteger(android.R.integer.config_mediumAnimTime) + 100;
    LayoutTransition transition = new LayoutTransition();
    transition.enableTransitionType(LayoutTransition.CHANGING);
    transition.setStartDelay(LayoutTransition.APPEARING, startDelay);
    transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, startDelay);
    loginForm.setLayoutTransition(transition);
  }

  @Override public ViewState createViewState() {
    return new LoginViewState();
  }

  @Override public LoginPresenter createPresenter() {
    return loginComponent.presenter();
  }

  @OnClick(R.id.loginButton) public void onLoginClicked() {

    // Check for empty fields
    String uname = username.getText().toString();
    String pass = password.getText().toString();

    loginForm.clearAnimation();

    if (TextUtils.isEmpty(uname)) {
      username.clearAnimation();
      Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
      username.startAnimation(shake);
      return;
    }

    if (TextUtils.isEmpty(pass)) {
      password.clearAnimation();
      Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
      password.startAnimation(shake);
      return;
    }

    // Hide keyboard
    if (!KeyboardUtils.hideKeyboard(username)) {
      KeyboardUtils.hideKeyboard(password);
    }

    // Start login
    presenter.doLogin(new AuthCredentials(uname, pass));
  }

  @Override public void onNewViewStateInstance() {
    showLoginForm();
  }

  @Override public void showLoginForm() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoginForm();

    errorView.setVisibility(View.GONE);
    setFormEnabled(true);
    loginButton.setProgress(0);
  }

  @Override public void showError() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowError();

    setFormEnabled(true);
    loginButton.setProgress(0);

    if (!isRestoringViewState()) {
      // Enable animations only if not restoring view state
      loginForm.clearAnimation();
      Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
      loginForm.startAnimation(shake);
    }

    errorView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoading();
    errorView.setVisibility(View.GONE);
    setFormEnabled(false);
    // any progress between 0 - 100 shows animation
    loginButton.setProgress(30);
  }

  private void setFormEnabled(boolean enabled) {
    username.setEnabled(enabled);
    password.setEnabled(enabled);
    loginButton.setEnabled(enabled);
  }

  @Override public void loginSuccessful() {
    loginButton.setProgress(100); // We are done
    getActivity().finish();
    getActivity().overridePendingTransition(0, R.anim.zoom_out);
  }

  @Override protected void injectDependencies() {
    loginComponent = DaggerLoginComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .build();
  }
}
