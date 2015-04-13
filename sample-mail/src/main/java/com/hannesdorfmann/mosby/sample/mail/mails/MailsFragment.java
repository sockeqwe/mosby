package com.hannesdorfmann.mosby.sample.mail.mails;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Toast;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.sample.mail.Intentomat;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.AuthRefreshRecyclerFragment;
import com.hannesdorfmann.mosby.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MailsFragment
    extends AuthRefreshRecyclerFragment<List<Mail>, MailsView, MailsPresenter>
    implements MailsView, MailsAdapter.MailClickedListener, MailsAdapter.MailStarListner {

  // TODO highlight selected element

  @Arg Label label;
  @Inject EventBus eventBus;

  MailsComponent mailsComponent;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mails;
  }

  @Override protected ListAdapter<List<Mail>> createAdapter() {
    return new MailsAdapter(getActivity(), this, this);
  }

  @Override protected MailsPresenter createPresenter() {
    return mailsComponent.presenter();
  }

  @Override public void loadData(boolean b) {
    presenter.load(b, label);
  }

  @Override public void onMailClicked(MailsAdapterHolders.MailViewHolder vh, Mail mail) {

    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
            Pair.create((View) vh.senderPic, getString(R.string.shared_mail_sender_pic)),
            Pair.create((View) vh.subject, getString(R.string.shared_mail_subject)),
            Pair.create((View) vh.date, getString(R.string.shared_mail_date)),
            Pair.create((View) vh.star, getString(R.string.shared_mail_star)),
            Pair.create(getActivity().findViewById(R.id.toolbar),
                getString(R.string.shared_mail_toolbar)));

    Intentomat.showMailDetails(getActivity(), mail, options.toBundle());
  }

  @Override public void onMailStarClicked(Mail mail) {
    presenter.starMail(mail, !mail.isStarred());
  }

  @Override protected void injectDependencies() {
    mailsComponent = DaggerMailsComponent.create();
    mailsComponent.inject(this);
  }

  @Override public void markMailAsStared(int mailId) {
    // Search for the mail
    Mail mail = ((MailsAdapter) adapter).findMail(mailId);
    if (mail != null) {
      mail.setStarred(true);
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void markMailAsUnstared(int mailId) {

    // Search for the mail
    Mail mail = ((MailsAdapter) adapter).findMail(mailId);
    if (mail != null) {
      mail.setStarred(false);
      adapter.notifyDataSetChanged();
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

  @Override public void changeLabel(int mailId, String label) {
    // TODO implement
  }

  @Override public void showChangeLabelFailed(Mail mail, Throwable t) {
    // TODO implement
  }
}
