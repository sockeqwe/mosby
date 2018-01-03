package com.hannesdorfmann.mosby3.sample.mail.write;

import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author Hannes Dorfmann
 */
public interface WriteView extends MvpView {

  void showForm();

  void showLoading();

  void showError(Throwable e);

  void showAuthenticationRequired();

  void finishBecauseSuccessful();
}
