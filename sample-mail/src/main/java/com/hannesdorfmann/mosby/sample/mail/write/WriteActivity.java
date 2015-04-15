package com.hannesdorfmann.mosby.sample.mail.write;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.MosbyActivity;
import com.hannesdorfmann.mosby.sample.mail.R;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21) public class WriteActivity extends MosbyActivity {

  public static final String KEY_REPLAY_MAIL="com.hannesdorfmann.mosby.sample.mail.write.REPLAY_MAIL";

  @InjectView(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write);

    if (isMinApi21()){
      getWindow().getEnterTransition()
      .excludeTarget(R.id.toolbar, true)
      .excludeTarget(android.R.id.statusBarBackground, true)
      .excludeTarget(android.R.id.navigationBarBackground, true);
    }

    toolbar.setNavigationIcon(getBackArrowDrawable());
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isMinApi21()) {
          finishAfterTransition();
        } else {
          finish();
        }
      }
    });
  }

  @TargetApi(21) private Drawable getBackArrowDrawable() {

    if (isMinApi21()) {
      return getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, getTheme());
    } else {
      return getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }
  }


  private boolean isMinApi21() {
    return Build.VERSION.SDK_INT >= 21;
  }
}
