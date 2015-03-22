package com.hannesdorfmann.mosby.sample.mvp.customviewstate;

import android.os.Bundle;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomViewState implements RestoreableViewState<MyCustomView> {

  private final String BUNDLE_KEY = "MyCustomViewState";

  public boolean showingA = true; // if false, then show B

  @Override public void saveInstanceState(Bundle out) {
    out.putBoolean(BUNDLE_KEY, showingA);
  }

  @Override public boolean restoreInstanceState(Bundle in) {
    if (in == null) {
      return false;
    }

    showingA = in.getBoolean(BUNDLE_KEY, true);
    return true;
  }

  @Override public void apply(MyCustomView view, boolean retained) {

    if (showingA) {
      view.showA();
    } else {
      view.showB();
    }
  }

  /**
   * @param a true if showing a, false if showing b
   */
  public void setShowingA(boolean a) {
    this.showingA = a;
  }
}
