package com.hannesdorfmann.mosby.sample.mail.details;

import android.os.Bundle;
import android.view.View;
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
import com.github.clans.fab.FloatingActionButton;


/**
 * @author Hannes Dorfmann
 */
public class DetailsFragment extends AuthFragment<View, Mail, DetailsView, DetailsPresenter>
    implements DetailsView {

  @Arg int mailId;

  @InjectView(R.id.header) View header;
  @InjectView(R.id.senderPic) ImageView senderPic;
  @InjectView(R.id.subject) TextView subject;
  @InjectView(R.id.date) TextView date;
  @InjectView(R.id.starButton) StarView star;
  @InjectView(R.id.replay) FloatingActionButton replay;
  @InjectView(R.id.message) TextView message;

  private Mail mail;

  @Override public AuthViewState<Mail, DetailsView> createViewState() {
    return new AuthParcelableDataViewState<>();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override protected boolean createOrRestoreViewState(Bundle savedInstanceState) {
    return super.createOrRestoreViewState(savedInstanceState);
  }

  @Override protected void saveViewStateInstanceState(Bundle outState) {
    super.saveViewStateInstanceState(outState);
  }

  @Override public Mail getData() {
    return mail;
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mail_details;
  }

  @Override protected DetailsPresenter createPresenter() {
    return DaggerDetailsComponent.create().presenter();
  }

  @Override public void setData(Mail data) {
    this.mail = data;
    senderPic.setImageResource(data.getSender().getImageRes());
    subject.setText(data.getSubject());
    message.setText(data.getText());
    star.setStarred(data.isStarred());
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadMail(mailId);
  }
}
