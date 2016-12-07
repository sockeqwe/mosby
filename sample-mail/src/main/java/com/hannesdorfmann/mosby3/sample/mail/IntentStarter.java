package com.hannesdorfmann.mosby3.sample.mail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.sample.mail.details.DetailsActivity;
import com.hannesdorfmann.mosby3.sample.mail.login.LoginActivity;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.service.SendMailService;
import com.hannesdorfmann.mosby3.sample.mail.profile.ProfileActivity;
import com.hannesdorfmann.mosby3.sample.mail.search.SearchActivity;
import com.hannesdorfmann.mosby3.sample.mail.write.WriteActivity;

/**
 * A simple helper class that helps to create and launch Intents. It checks if we our device is a
 * phone or a tablet app.
 *
 * @author Hannes Dorfmann
 */
// TODO make it injectable with dagger
public class IntentStarter {

  private boolean isTablet(Context context) {
    return context.getResources().getBoolean(R.bool.tablet);
  }

  public void showMailDetails(Context context, Mail mail, Bundle activityTransitionBundle) {

    Intent i = null;
    if (isTablet(context)) {
      i = new Intent(context, MainActivity.class);
      i.putExtra(MainActivity.KEY_SHOW_ACTION, MainActivity.KEY_SHOW_ACTION_MAIL_DETAILS);
      i.putExtra(MainActivity.KEY_DATA_MAIL_DETAILS, mail);
    } else {
      i = getShowMailInNewActivityIntent(context, mail);
    }

    context.startActivity(i, activityTransitionBundle);
  }

  public Intent getShowMailInNewActivityIntent(Context context, Mail mail) {
    Intent i = new Intent(context, DetailsActivity.class);
    i.putExtra(DetailsActivity.KEY_MAIL, mail);
    return i;
  }

  public void showMailsOfLabel(Context context, Label label) {
    Intent i = new Intent(context, MainActivity.class);
    i.putExtra(MainActivity.KEY_SHOW_ACTION, MainActivity.KEY_SHOW_ACTION_MAILS_OF_LABEL);
    i.putExtra(MainActivity.KEY_DATA_MAILS_OF_LABEL, label);

    context.startActivity(i);
  }

  public void showWriteMail(Context context, Mail replayTo, Bundle activityTransitionBundle) {

    Intent i = new Intent(context, WriteActivity.class);

    if (replayTo != null) {
      i.putExtra(WriteActivity.KEY_REPLAY_MAIL, replayTo);
    }

    context.startActivity(i, activityTransitionBundle);
  }

  public void showAuthentication(Context context) {
    context.startActivity(new Intent(context, LoginActivity.class));
  }

  public void sendMailViaService(Context context, Mail mail) {
    Intent i = new Intent(context, SendMailService.class);
    i.putExtra(SendMailService.KEY_MAIL, mail);
    context.startService(i);
  }

  public void showSearch(Activity context) {
    Intent i = new Intent(context, SearchActivity.class);
    context.startActivity(i);
    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  public void showProfile(Context context, Person person) {
    Intent i = new Intent(context, ProfileActivity.class);
    i.putExtra(ProfileActivity.KEY_PERSON, person);
    context.startActivity(i);
  }

}
