package com.hannesdorfmann.mosby.sample.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.hannesdorfmann.mosby.sample.mail.details.DetailsActivity;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.write.WriteActivity;

/**
 * A simple helper class that helps to create and launch Intents. It checks if we our device is a
 * phone or a tablet app.
 *
 * @author Hannes Dorfmann
 */
// TODO make it injectable with dagger
public class Intentomat {

  private static boolean isTablet(Context context) {
    return context.getResources().getBoolean(R.bool.tablet);
  }

  public static void showMailDetails(Context context, Mail mail, Bundle activityTransitionBundle) {

    Intent i = null;
    if (isTablet(context)) {
      i = new Intent(context, MainActivity.class);
      i.putExtra(MainActivity.KEY_SHOW_ACTION, MainActivity.KEY_SHOW_ACTION_MAIL_DETAILS);
      i.putExtra(MainActivity.KEY_DATA_MAIL_DETAILS, mail);
    } else {
      i = new Intent(context, DetailsActivity.class);
      i.putExtra(DetailsActivity.KEY_MAIL, mail);
    }

    context.startActivity(i, activityTransitionBundle);
  }

  public static void showMailsOfLabel(Context context, Label label) {
    Intent i = new Intent(context, MainActivity.class);
    i.putExtra(MainActivity.KEY_SHOW_ACTION, MainActivity.KEY_SHOW_ACTION_MAILS_OF_LABEL);
    i.putExtra(MainActivity.KEY_DATA_MAILS_OF_LABEL, label);

    context.startActivity(i);
  }


  public static void showWriteMail(Context context, Mail replayTo, int x, int y, int radius) {

    Intent i = new Intent(context, WriteActivity.class);
    i.putExtra(WriteActivity.KEY_REVEAL_START_X, x);
    i.putExtra(WriteActivity.KEY_REVEAL_START_Y, y);
    i.putExtra(WriteActivity.KEY_REVEAL_START_RADIUS, radius);

    if (replayTo != null) {
      i.putExtra(WriteActivity.KEY_REPLAY_MAIL, replayTo);
    }

    context.startActivity(i);
  }
}
