package com.hannesdorfmann.mosby.sample.mail.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Explode;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.AuthFragment;
import com.hannesdorfmann.mosby.sample.mail.base.view.viewstate.AuthParcelableDataViewState;
import com.hannesdorfmann.mosby.sample.mail.base.view.viewstate.AuthViewState;
import com.hannesdorfmann.mosby.sample.mail.label.LabelLayout;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.ui.transition.ExplodeFadeEnterTransition;
import com.hannesdorfmann.mosby.sample.mail.ui.transition.TextSizeEnterSharedElementCallback;
import com.hannesdorfmann.mosby.sample.mail.ui.transition.TextSizeTransition;
import com.hannesdorfmann.mosby.sample.mail.ui.view.StarView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
public class DetailsFragment extends AuthFragment<TextView, Mail, DetailsView, DetailsPresenter>
    implements DetailsView, View.OnClickListener {

  @Arg int mailId;
  @Arg String subject;
  @Arg int senderProfilePic;
  @Arg String senderName;
  @Arg String senderEmail;
  @Arg long date;
  @Arg boolean starred;

  @InjectView(R.id.senderPic) ImageView senderImageView;
  @InjectView(R.id.subject) TextView subjectView;
  @InjectView(R.id.date) TextView dateView;
  @InjectView(R.id.starButton) StarView starView;
  @InjectView(R.id.replay) FloatingActionButton replayView;
  @InjectView(R.id.senderName) TextView senderNameView;
  @InjectView(R.id.senderMail) TextView senderMailView;
  @InjectView(R.id.separatorLine) View separatorLine;
  @InjectView(R.id.label) LabelLayout labelView;
  @InjectView(R.id.scrollView) ObservableScrollView scrollView;

  Format format = new SimpleDateFormat("d. MMM");

  // The loaded data
  private Mail mail;

  @Override public AuthViewState<Mail, DetailsView> createViewState() {
    return new AuthParcelableDataViewState<>();
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mail_details;
  }

  @TargetApi(21) @Override
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    replayView.attachToScrollView(scrollView);
    starView.setOnClickListener(this);

    subjectView.setText(subject);
    senderImageView.setImageResource(senderProfilePic);
    senderNameView.setText(senderName);
    senderMailView.setText(senderEmail);
    starView.setStarred(starred);
    dateView.setText(format.format(new Date(date)));

    // Shared element animation
    if (Build.VERSION.SDK_INT >= 21 && !isTablet()) {

      initTransitions();

      view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override public boolean onPreDraw() {
          view.getViewTreeObserver().removeOnPreDrawListener(this);
          getActivity().startPostponedEnterTransition();
          return true;
        }
      });
    }
  }

  @TargetApi(21) private void initTransitions() {

    Window window = getActivity().getWindow();
    window.setEnterTransition(
        new ExplodeFadeEnterTransition(senderNameView, senderMailView, separatorLine));
    window.setExitTransition(new Explode());
    window.setReenterTransition(new Explode());
    window.setReturnTransition(new Explode());

    TransitionSet textSizeSet = new TransitionSet();
    textSizeSet.addTransition(
        TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
    TextSizeTransition textSizeTransition = new TextSizeTransition();
    textSizeTransition.addTarget(R.id.subject);
    textSizeTransition.addTarget(getString(R.string.shared_mail_subject));

    textSizeSet.addTransition(textSizeTransition);
    textSizeSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

    window.setSharedElementEnterTransition(textSizeSet);
    getActivity().setEnterSharedElementCallback(
        new TextSizeEnterSharedElementCallback(getActivity()));
  }

  private boolean isTablet() {
    return getResources().getBoolean(R.bool.tablet);
  }

  @Override public Mail getData() {
    return mail;
  }

  @Override protected DetailsPresenter createPresenter() {
    return DaggerDetailsComponent.create().presenter();
  }

  @Override public void setData(Mail data) {
    this.mail = data;

    senderImageView.setImageResource(data.getSender().getImageRes());
    senderNameView.setText(data.getSender().getName());
    senderMailView.setText(data.getSender().getEmail());
    subjectView.setText(data.getSubject());
    contentView.setText(data.getText() + data.getText() + data.getText() + data.getText());
    starView.setStarred(data.isStarred());
    dateView.setText(format.format(data.getDate()));
    labelView.setMail(data);
    labelView.setVisibility(View.VISIBLE);
    replayView.setVisibility(View.VISIBLE);

    // Animate only if not restoring
    if (!isRestoringViewState()) {
      labelView.setAlpha(0f);
      labelView.animate().alpha(1f).setDuration(150).start();

      PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
      PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
      ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(replayView, holderX, holderY);
      animator.setInterpolator(new OvershootInterpolator());
      animator.start();
    }
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadMail(mailId);
  }

  public int getMailId() {
    return mailId;
  }

  @Override public void onClick(View v) {
    if (mail != null) {
      presenter.starMail(mail, !mail.isStarred());
    } else {
      Toast.makeText(getActivity(), R.string.error_wait_mail_loaded, Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void markMailAsStared(int mailId) {
    if (mail.getId() == mailId) {
      mail.setStarred(true);
      starView.setStarred(true);
    }
  }

  @Override public void markMailAsUnstared(int mailId) {
    if (mail.getId() == mailId) {
      mail.setStarred(false);
      starView.setStarred(false);
    }
  }

  private void showStarErrorToast(int messageRes, Mail mail) {
    Toast.makeText(getActivity(), String.format(getString(messageRes), mail.getSender().getName()),
        Toast.LENGTH_SHORT).show();
  }

  @Override public void showStaringFailed(Mail mail) {
    showStarErrorToast(R.string.error_staring_mail, mail);
  }

  @Override public void showUnstaringFailed(Mail mail) {
    showStarErrorToast(R.string.error_unstaring_mail, mail);
  }

  @Override public void changeLabel(Mail mail, String label) {
    // In a real world app shouldn't be any empty methods.
    // I'm just to lazy
  }

  @OnClick(R.id.replay) public void onReplayClicked() {

    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), replayView,
            getString(R.string.shared_write_action));

    IntentStarter.showWriteMail(getActivity(), mail, options.toBundle());
  }
}
