package com.hannesdorfmann.mosby.sample.mail.details;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.AuthFragment;
import com.hannesdorfmann.mosby.sample.mail.base.view.viewstate.AuthParcelableDataViewState;
import com.hannesdorfmann.mosby.sample.mail.base.view.viewstate.AuthViewState;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.ui.event.view.StarView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
public class DetailsFragment extends AuthFragment<TextView, Mail, DetailsView, DetailsPresenter>
    implements DetailsView {

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
  @InjectView(R.id.label) View labelView;
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

  @TargetApi(21)
  @Override public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    replayView.attachToScrollView(scrollView);


    subjectView.setText(subject);
    senderImageView.setImageResource(senderProfilePic);
    senderNameView.setText(senderName);
    senderMailView.setText(senderEmail);
    starView.setStarred(starred);
    dateView.setText(format.format(new Date(date)));

    // Shared element animation
    if (Build.VERSION.SDK_INT >= 21) {
      view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override public boolean onPreDraw() {
          view.getViewTreeObserver().removeOnPreDrawListener(this);
          getActivity().startPostponedEnterTransition();
          return true;
        }
      });
    }
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
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadMail(mailId);
  }

  public int getMailId() {
    return mailId;
  }
}
