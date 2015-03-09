package com.hannesdorfmann.mosby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import icepick.Icepick;

/**
 * A base fragment that uses Icepick, Butterknife and FragmentArgs.
 * Instead of overriding {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} you can also
 * simply have
 * override {@link #getLayoutRes()} and return your desired layout resource, which will be
 * inflated.
 * <p>
 * Future initialization can be done in {@link #onViewCreated(View, Bundle)} method <b>(don't forget
 * to
 * call super.onViewCreated())</b>, which is called after
 * the view has been created, Butterknife has "injected" views, FragmentArgs has been set and
 * Icepick has restored savedInstanceState.
 * <code>init()</code> is called from {@link #onViewCreated(View, Bundle)} which is called after
 * {@link #onCreateView(LayoutInflater, ViewGroup,
 * Bundle)}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class MosbyFragment extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FragmentArgs.inject(this);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    Integer layoutRes = getLayoutRes();
    if (layoutRes == null) {
      return null;
    } else {
      View v = inflater.inflate(getLayoutRes(), container, false);
      return v;
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);
  }

  /**
   * Return the layout resource like R.layout.my_layout
   *
   * @return the layout resource or null, if you don't want to have an UI
   */
  protected Integer getLayoutRes() {
    return null;
  }
}
