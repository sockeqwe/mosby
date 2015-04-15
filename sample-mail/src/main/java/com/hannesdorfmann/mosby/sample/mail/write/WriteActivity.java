package com.hannesdorfmann.mosby.sample.mail.write;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.View;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.ui.transition.RevealTransition;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21) public class WriteActivity extends MosbyActivity {

  public static final String KEY_REPLAY_MAIL = "com.hannesdorfmann.mosby.sample.mail.write.REPLAY";
  public static final String KEY_REVEAL_START_X =
      "com.hannesdorfmann.mosby.sample.mail.write.REVEAL_X";
  public static final String KEY_REVEAL_START_Y =
      "com.hannesdorfmann.mosby.sample.mail.write.REVEAL_Y";
  public static final String KEY_REVEAL_START_RADIUS =
      "com.hannesdorfmann.mosby.sample.mail.write.REVEAL_RADIUS";

  @InjectView(R.id.color_overlay) View colorOverlay;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write);

    if (isMinApi21()) {
      setupTransition();
    }
  }

  @TargetApi(21) private void setupTransition() {

    Point startPoint = new Point(getIntent().getIntExtra(KEY_REVEAL_START_X, 0),
        getIntent().getIntExtra(KEY_REVEAL_START_Y, 0));

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int endRadius = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);

    Transition transition = new RevealTransition(startPoint, 0, endRadius, 3000);
    transition.addTarget(R.id.color_overlay);

    getWindow().setEnterTransition(transition);
  }

  private boolean isMinApi21() {
    return Build.VERSION.SDK_INT >= 21;
  }
}
