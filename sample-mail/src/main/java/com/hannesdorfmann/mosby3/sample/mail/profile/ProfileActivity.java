package com.hannesdorfmann.mosby3.sample.mail.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.astuetz.PagerSlidingTabStrip;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.ParcelableLceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseLceActivity;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.ProfileScreen;
import com.hannesdorfmann.mosby3.sample.mail.utils.BuildUtils;
import com.hannesdorfmann.mosby3.sample.mail.utils.MathUtils;
import github.chenupt.dragtoplayout.DragTopLayout;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class ProfileActivity
    extends BaseLceActivity<DragTopLayout, List<ProfileScreen>, ProfileView, ProfilePresenter>
    implements ProfileView {

  public static final String KEY_PERSON =
      "com.hannesdorfmann.mosby.sample.mail.profile.ProfileActivity.PERSON";

  private Person person;
  private ProfileScreensAdapter adapter;
  private ProfileComponent profileComponent;
  @Bind(R.id.viewPager) ViewPager viewPager;
  @Bind(R.id.tabs) PagerSlidingTabStrip tabs;
  @Bind(R.id.fadingToolbarHelper) View fadingToolbarHelper;
  @Bind(R.id.separatorLine) View separatorLine;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.toolbarTitle) TextView toolbarTitle;
  @Bind(R.id.profileHeaderPic) ImageView headerImage;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    person = getIntent().getParcelableExtra(KEY_PERSON);
    headerImage.setImageResource(person.getImageRes());
    headerImage.setColorFilter(Color.parseColor("#32000000"), PorterDuff.Mode.SRC_ATOP);

    toolbarTitle.setText(person.getName());

    toolbar.setNavigationIcon(BuildUtils.getBackArrowDrawable(this));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    contentView.listener(new DragTopLayout.SimplePanelListener() {

      @Override public void onSliding(float v) {
        fadingToolbarHelper.setAlpha(1f - v);
        headerImage.setTranslationY(MathUtils.mapPoint(v, 0f, 1f,
            -(headerImage.getHeight() - toolbar.getHeight() - tabs.getHeight()), 0f) * 0.5f);

        toolbarTitle.setAlpha(MathUtils.mapPoint(v, 0, 0.4f, 1f, 0f));
        separatorLine.setAlpha(MathUtils.mapPoint(v, 0, 0.4f, 1f, 0f));
      }
    });
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return getString(R.string.error_has_occurred);
  }

  @Override public ProfilePresenter createPresenter() {
    return profileComponent.presenter();
  }

  @Override public void setData(List<ProfileScreen> data) {

    adapter = new ProfileScreensAdapter(getSupportFragmentManager(), person);
    adapter.setScreens(data);
    viewPager.setAdapter(adapter);
    tabs.setViewPager(viewPager);
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadScreens(person);
  }

  @Override public ParcelableLceViewState<List<ProfileScreen>, ProfileView> createViewState() {
    return new CastedArrayListLceViewState<>();
  }

  @Override public List<ProfileScreen> getData() {
    return adapter == null ? null : adapter.getScreens();
  }

  @Override protected void animateContentViewIn() {
    if (contentView.getVisibility() != View.VISIBLE) {
      AnimatorSet animations = new AnimatorSet();
      animations.playTogether(ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(loadingView, "alpha", 1f, 0f),
          ObjectAnimator.ofFloat(headerImage, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(toolbar, "alpha", 0f, 1f)

      );
      animations.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationStart(Animator animation) {
          contentView.setVisibility(View.VISIBLE);
        }

        @Override public void onAnimationEnd(Animator animation) {
          loadingView.setVisibility(View.GONE);
        }
      });
      animations.setDuration(500);
      animations.start();
    } else {
      contentView.setVisibility(View.VISIBLE);
      errorView.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
      toolbar.setAlpha(1f);
      headerImage.setVisibility(View.VISIBLE);
      headerImage.setAlpha(1f);
    }
  }

  @Override protected void injectDependencies() {
    profileComponent = DaggerProfileComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .build();
  }
}
