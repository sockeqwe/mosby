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

package com.hannesdorfmann.mosby3.sample.mail.base.view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.viewstate.AuthViewState;
import javax.inject.Inject;

/**
 * Base fragment that handles displaying Authentication state and LCE. Also includes Butterknife,
 * FragmentArgs and Icepick
 *
 * @author Hannes Dorfmann
 */
public abstract class AuthFragment<AV extends View, M, V extends AuthView<M>, P extends MvpPresenter<V>>
    extends BaseLceFragment<AV, M, V, P> implements AuthView<M> {

  protected View authView;
  @Inject IntentStarter intentStarter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FragmentArgs.inject(this);
  }

  @LayoutRes protected abstract int getLayoutRes();

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(getLayoutRes(), container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    authView = view.findViewById(R.id.authView);
    authView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onAuthViewClicked();
      }
    });
  }

  protected void onAuthViewClicked() {
    intentStarter.showAuthentication(getActivity());
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    if (pullToRefresh) {
      return "An error has occurred!";
    } else {
      return "An error has occurred. Click here to retry";
    }
  }

  @Override public void showAuthenticationRequired() {
    AuthViewState vs = (AuthViewState) viewState;
    vs.setShowingAuthenticationRequired();

    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    contentView.setVisibility(View.GONE);
    authView.setVisibility(View.VISIBLE);
  }

  @Override public void showContent() {
    super.showContent();
    authView.setVisibility(View.GONE);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    authView.setVisibility(View.GONE);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    authView.setVisibility(View.GONE);
  }

  @Override public abstract AuthViewState<M, V> createViewState();
}
