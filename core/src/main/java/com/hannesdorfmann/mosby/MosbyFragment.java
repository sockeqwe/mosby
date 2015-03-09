package com.hannesdorfmann.mosby;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import icepick.Icepick;

/**
 * A base fragment that uses Icepick, Butterknife and FragmentArgs.
 * Instead of overriding {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} you simply have
 * to
 * implement {@link #getLayoutRes()} and the given layout resource will be inflated automatically.
 * <p>
 * Future initialization can be done in {@link #init(View, Bundle)} method, which is called after
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

    Icepick.restoreInstanceState(this, savedInstanceState);

    Integer layoutRes = getLayoutRes();
    if (layoutRes == null) {
      return null;
    } else {
      View v = inflater.inflate(getLayoutRes(), container, false);
      ButterKnife.inject(this, v);
      return v;
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init(view, savedInstanceState);
  }

  /**
   * Called to initialize the Mosby fragment. It will be called while {@link
   * #onCreateView(LayoutInflater, ViewGroup, Bundle)}. Here is where you should do some
   * initialization staff.
   *
   * @param view The inflated view. Can be null if {@link #getLayoutRes()} return null.
   * @param savedInstance The saved instance state bundle. Can be null if no saved instance state
   * is
   * available
   */
  public abstract void init(@Nullable View view, @Nullable Bundle savedInstance);

  /**
   * Return the layout resource like R.layout.my_layout
   *
   * @return the layout resource or null, if you don't want to have an UI
   */
  protected abstract Integer getLayoutRes();
}
