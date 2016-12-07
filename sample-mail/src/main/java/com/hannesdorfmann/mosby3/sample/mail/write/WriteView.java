package com.hannesdorfmann.mosby3.sample.mail.write;

import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author Hannes Dorfmann
 */
public interface WriteView extends MvpView {

  public void showForm();

  public void showLoading();

  public void showError(Throwable e);

  public void showAuthenticationRequired();

  public void finishBecauseSuccessful();
}
