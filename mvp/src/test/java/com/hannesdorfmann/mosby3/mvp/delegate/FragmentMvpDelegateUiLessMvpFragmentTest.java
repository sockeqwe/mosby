package com.hannesdorfmann.mosby3.mvp.delegate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

/**
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricTestRunner.class) @Config(manifest = Config.NONE)
public class FragmentMvpDelegateUiLessMvpFragmentTest {

  public static class UiLessFragment extends MvpFragment<MvpView, MvpPresenter<MvpView>> {

    @Override public MvpPresenter<MvpView> createPresenter() {
      return new MvpBasePresenter<MvpView>();
    }
  }

  public static class CorrectUiFragment extends MvpFragment<MvpView, MvpPresenter<MvpView>> {

    @Override public MvpPresenter<MvpView> createPresenter() {
      return new MvpBasePresenter<MvpView>();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
      return new View(RuntimeEnvironment.application);
    }
  }

  @Test() public void uiLessShouldFail() {
    try {
      SupportFragmentTestUtil.startVisibleFragment(new UiLessFragment());
      Assert.fail("Exception expected");
    } catch (IllegalStateException e) {
      Assert.assertEquals(
          "It seems that you are using "+UiLessFragment.class.getCanonicalName()+" as headless (UI less) fragment (because onViewCreated() has not been called or maybe delegation misses that part). Having a Presenter without a View (UI) doesn't make sense. Simply use an usual fragment instead of an MvpFragment if you want to use a UI less Fragment",
          e.getMessage());
    }
  }

  @Test public void correctUi() {
    SupportFragmentTestUtil.startVisibleFragment(new CorrectUiFragment());
  }
}
