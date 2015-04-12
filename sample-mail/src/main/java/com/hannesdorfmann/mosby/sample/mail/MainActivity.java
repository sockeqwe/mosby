package com.hannesdorfmann.mosby.sample.mail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.Optional;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mail.details.DetailsFragmentBuilder;
import com.hannesdorfmann.mosby.sample.mail.mails.MailsFragmentBuilder;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import com.hannesdorfmann.mosby.sample.mail.ui.event.ShowMailDetailsEvent;
import com.hannesdorfmann.mosby.sample.mail.ui.event.ShowMailsOfLabelEvent;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

public class MainActivity extends MosbyActivity {

  @Inject EventBus eventBus;

  @InjectView(R.id.drawerLayout) DrawerLayout drawerLayout;
  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.leftPane) ViewGroup leftPane;
  @Optional @InjectView(R.id.rightPane) ViewGroup rightPane;

  ActionBarDrawerToggle drawerToggle;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // UI
    setSupportActionBar(toolbar);
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
        R.string.drawer_close);
    drawerLayout.setDrawerListener(drawerToggle);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    // Listeners
    eventBus.register(this);

    // start with
    if (savedInstanceState == null) {
      showMails(MailProvider.INBOX_LABEL);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    eventBus.unregister(this);
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

  @Override protected void injectDependencies() {
    DaggerMainActivityComponent.create().inject(this);
  }

  public void onEventMainThread(ShowMailsOfLabelEvent event) {
    if (drawerLayout.isDrawerOpen(Gravity.START)) {
      drawerLayout.closeDrawer(Gravity.START);
    }
    showMails(event.getLabel());
  }

  private void showMails(Label label) {
    getSupportActionBar().setTitle(label.getName());

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.leftPane, new MailsFragmentBuilder(label).build())
        .commit();
  }

  public void onEventMainThread(ShowMailDetailsEvent event) {
    if (rightPane != null) {
      // TODO implement
    } else {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.leftPane, new DetailsFragmentBuilder(event.getMail().getId()).build())
          .addToBackStack(null)
          .commit();
      // TODO animation
    }
  }
}
