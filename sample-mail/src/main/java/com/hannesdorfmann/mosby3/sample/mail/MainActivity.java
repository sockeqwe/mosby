package com.hannesdorfmann.mosby3.sample.mail;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseActivity;
import com.hannesdorfmann.mosby3.sample.mail.details.DetailsFragment;
import com.hannesdorfmann.mosby3.sample.mail.details.DetailsFragmentBuilder;
import com.hannesdorfmann.mosby3.sample.mail.mails.MailsFragment;
import com.hannesdorfmann.mosby3.sample.mail.mails.MailsFragmentBuilder;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import icepick.Icepick;
import icepick.Icicle;
import javax.inject.Inject;

public class MainActivity extends BaseActivity {

  public static final String KEY_SHOW_ACTION =
      "com.hannesdorfmann.mosby.sample.mail.MainActivity.SHOW_ACTION";

  public static final String KEY_SHOW_ACTION_MAIL_DETAILS =
      "com.hannesdorfmann.mosby.sample.mail.MainActivity.SHOW_ACTION_MAIL_DETAILS";
  public static final String KEY_DATA_MAIL_DETAILS =
      "com.hannesdorfmann.mosby.sample.mail.MainActivity.MAIL";

  public static final String KEY_SHOW_ACTION_MAILS_OF_LABEL =
      "com.hannesdorfmann.mosby.sample.mail.MainActivity.SHOW_ACTION_MAILS_OF_LABEL";
  public static final String KEY_DATA_MAILS_OF_LABEL =
      "com.hannesdorfmann.mosby.sample.mail.MainActivity.LABEL";

  public static final String FRAGMENT_TAG_DETAILS = "detailsFragmentTag";
  public static final String FRAGMENT_TAG_LABEL = "labelFragmentTag";

  Fragment detailsFragment;
  MailsFragment labelFragment;

  @Icicle String toolbarTitle;

  @Inject IntentStarter intentStarter;

  @Bind(R.id.drawerLayout) DrawerLayout drawerLayout;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.leftPane) ViewGroup leftPane;
  @Nullable @Bind(R.id.rightPane) ViewGroup rightPane;
  // contains leftPane + rightPane
  @Nullable @Bind(R.id.paneContainer) ViewGroup paneContainer;

  ActionBarDrawerToggle drawerToggle;
  private MainActivityComponent mainActivityComponent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Toolbar
    toolbar.inflateMenu(R.menu.search_menu);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.search) {
          intentStarter.showSearch(MainActivity.this);
          return true;
        }
        return false;
      }
    });
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
        R.string.drawer_close);
    drawerLayout.setDrawerListener(drawerToggle);
    if (toolbarTitle != null) {
      toolbar.setTitle(toolbarTitle);
    }

    // Check for previous fragments
    detailsFragment = findDetailsFragment();
    labelFragment =
        (MailsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_LABEL);

    if (detailsFragment != null) {
      // details fragment available, so make it visible
      rightPane.setVisibility(View.VISIBLE);
    }

    if (paneContainer != null) {
      // Enable animation
      LayoutTransition transition = new LayoutTransition();
      transition.enableTransitionType(LayoutTransition.CHANGING);
      paneContainer.setLayoutTransition(transition);
    }

    if (labelFragment == null) {
      // First app start, so start with this
      showMails(MailProvider.INBOX_LABEL, true);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    String showAction = intent.getStringExtra(KEY_SHOW_ACTION);

    if (KEY_SHOW_ACTION_MAIL_DETAILS.equals(showAction)) {
      Mail mail = intent.getParcelableExtra(KEY_DATA_MAIL_DETAILS);
      showMail(mail);
    } else if (KEY_SHOW_ACTION_MAILS_OF_LABEL.equals(showAction)) {
      Label label = intent.getParcelableExtra(KEY_DATA_MAILS_OF_LABEL);
      showMails(label, true);
    }
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  private void showMails(Label label, boolean removeDetailsFragment) {
    toolbarTitle = label.getName();
    toolbar.setTitle(toolbarTitle);
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    }

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.leftPane, new MailsFragmentBuilder(label).build(), FRAGMENT_TAG_LABEL)
        .commit();

    if (removeDetailsFragment) {
      removeDetailsFragment();
    }
  }

  private void showMail(Mail mail) {

    rightPane.setVisibility(View.VISIBLE);
    Person sender = mail.getSender();
    DetailsFragment fragment =
        new DetailsFragmentBuilder(mail.getDate().getTime(), mail.getId(), sender.getEmail(),
            sender.getName(), sender.getImageRes(), mail.isStarred(), mail.getSubject()).build();

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.rightPane, fragment, FRAGMENT_TAG_DETAILS)
        .commit();
  }

  private Fragment findDetailsFragment() {
    return getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DETAILS);
  }

  /**
   * @return true if a fragment has been removed, otherwise false
   */
  private boolean removeDetailsFragment() {
    Fragment detailsFragment = findDetailsFragment();
    if (detailsFragment != null) {
      rightPane.setVisibility(View.GONE);
      getSupportFragmentManager().beginTransaction().remove(detailsFragment).commit();
      return true;
    }

    return false;
  }

  @Override public void onBackPressed() {

    // TODO make this easier to read
    if (!removeDetailsFragment()) {
      super.onBackPressed();
    }
  }

  protected void injectDependencies() {
    mainActivityComponent = DaggerMainActivityComponent.create();
    mainActivityComponent.inject(this);
  }
}
