package com.hannesdorfmann.mosby.sample.mail.write;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby.sample.mail.MailApplication;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.utils.BuildUtils;
import java.util.Date;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
@TargetApi(21) public class WriteActivity extends MvpViewStateActivity<WriteView, WritePresenter>
    implements WriteView {

  public static final String KEY_REPLAY_MAIL =
      "com.hannesdorfmann.mosby.sample.mail.write.REPLAY_MAIL";

  @InjectView(R.id.loadingOverlay) View loadingOverlay;
  @InjectView(R.id.authOverlay) View authOverlay;
  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.message) EditText message;
  @InjectView(R.id.subject) EditText subject;
  @InjectView(R.id.receiver) EditText receiver;

  @Inject IntentStarter intentStarter;

  private WriteComponent writeComponent;

  private Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write);

    if (BuildUtils.isMinApi21()) {
      getWindow().getEnterTransition()
          .excludeTarget(R.id.toolbar, true)
          .excludeTarget(android.R.id.statusBarBackground, true)
          .excludeTarget(android.R.id.navigationBarBackground, true);
    }

    toolbar.setNavigationIcon(getBackArrowDrawable());
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (BuildUtils.isMinApi21()) {
          finishAfterTransition();
        } else {
          finish();
        }
      }
    });

    Mail replayMail = getIntent().getParcelableExtra(KEY_REPLAY_MAIL);
    if (replayMail != null) {
      if (TextUtils.isEmpty(receiver.getText().toString())) {
        receiver.setText(replayMail.getSender().getEmail());
      }

      if (TextUtils.isEmpty(subject.getText().toString())) {
        subject.setText("RE: " + replayMail.getSubject());
      }
    }
  }

  @TargetApi(21) private Drawable getBackArrowDrawable() {

    if (BuildUtils.isMinApi21()) {
      return getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, getTheme());
    } else {
      return getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }
  }

  @Override public RestoreableViewState createViewState() {
    return new WriteViewState();
  }

  @Override public WritePresenter createPresenter() {
    return writeComponent.presenter();
  }

  @Override public void onNewViewStateInstance() {
    showForm();
  }

  @Override public WriteViewState getViewState() {
    return (WriteViewState) super.getViewState();
  }

  @Override public void showForm() {
    getViewState().setStateShowForm();
    loadingOverlay.setVisibility(View.GONE);
    authOverlay.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    getViewState().setStateShowLoading();
    authOverlay.setVisibility(View.GONE);
    loadingOverlay.setVisibility(View.VISIBLE);
    if (!restoringViewState) {
      loadingOverlay.setAlpha(0f);
      loadingOverlay.animate().alpha(1f).setDuration(200).start();
    }
  }

  @Override public void showError(Throwable e) {
    Toast.makeText(this, R.string.error_sending_mail, Toast.LENGTH_SHORT).show();
    showForm();
  }

  @Override public void showAuthenticationRequired() {
    getViewState().setStateAuthenticationRequired();
    loadingOverlay.setVisibility(View.GONE);
    authOverlay.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.authView) public void onAuthViewClicked() {
    intentStarter.showAuthentication(this);
  }

  @OnClick(R.id.send) public void onSendClicked() {

    String email = receiver.getText().toString();
    if (TextUtils.isEmpty(email) || !emailPattern.matcher(email).matches()) {
      Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
      receiver.startAnimation(shake);
      return;
    }

    String sub = subject.getText().toString();
    if (TextUtils.isEmpty(sub)) {
      Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
      subject.startAnimation(shake);
      return;
    }

    Person receiver = null;
    if (email.equals(Person.BARNEY.getEmail())) {
      receiver = Person.BARNEY;
    } else if (email.equals(Person.LILY.getEmail())) {
      receiver = Person.LILY;
    } else if (email.equals(Person.MARSHALL.getEmail())) {
      receiver = Person.MARSHALL;
    } else if (email.equals(Person.ROBIN.getEmail())) {
      receiver = Person.ROBIN;
    } else {
      String name = email.split("@")[0];
      receiver = new Person(23, name, email, R.drawable.unknown, null, 0);
    }

    String text = message.getText().toString();

    Mail mail = new Mail().date(new Date())
        .label(Label.SENT)
        .sender(Person.TED)
        .receiver(receiver)
        .subject(sub)
        .text(text);

    presenter.writeMail(getApplicationContext(), mail);
  }

  @Override public void finishBecauseSuccessful() {
    if (BuildUtils.isMinApi21()) {
      finishAfterTransition();
    } else {
      finish();
    }
  }

  @Override protected void injectDependencies() {
    writeComponent = DaggerWriteComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build();

    writeComponent.inject(this);
  }
}
