package com.hannesdorfmann.mosby.sample.mail.write;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * @author Hannes Dorfmann
 */
public class WriteViewState implements RestorableViewState<WriteView> {

  private final String KEY_STATE =
      "com.hannesdorfmann.mosby.sample.mail.write.WriteViewState.current_State";

  private final int STATE_SHOWING_FORM = 0;
  private final int STATE_SHOWING_LOADING = 1;
  private final int STATE_SHOWING_AUTH_REQUIRED = -1;

  private int currentState = 0;

  @Override public void saveInstanceState(Bundle out) {
    out.putInt(KEY_STATE, currentState);
  }

  @Override public RestorableViewState<WriteView> restoreInstanceState(Bundle in) {
    currentState = in.getInt(KEY_STATE);
    return this;
  }

  @Override public void apply(WriteView view, boolean retained) {
    if (currentState == STATE_SHOWING_FORM) {
      view.showForm();
    } else if (currentState == STATE_SHOWING_AUTH_REQUIRED) {
      view.showAuthenticationRequired();
    } else {
      view.showLoading();
    }
  }

  public void setStateShowForm() {
    currentState = STATE_SHOWING_FORM;
  }

  public void setStateShowLoading() {
    currentState = STATE_SHOWING_LOADING;
  }

  public void setStateAuthenticationRequired() {
    currentState = STATE_SHOWING_AUTH_REQUIRED;
  }
}
