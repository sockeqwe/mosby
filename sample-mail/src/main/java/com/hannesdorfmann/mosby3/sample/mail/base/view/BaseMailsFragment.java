package com.hannesdorfmann.mosby3.sample.mail.base.view;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Toast;
import com.hannesdorfmann.mosby3.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby3.sample.mail.mails.MailsAdapter;
import com.hannesdorfmann.mosby3.sample.mail.mails.MailsAdapterHolders;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;
import javax.inject.Inject;

/**
 * Base class for fragments that want to dipslay a list of Mails
 *
 * @author Hannes Dorfmann
 */
public abstract class BaseMailsFragment<V extends BaseMailView<List<Mail>>, P extends BaseRxMailPresenter<V, List<Mail>>>
    extends AuthRefreshRecyclerFragment<List<Mail>, V, P>
    implements BaseMailView<List<Mail>>, MailsAdapter.MailClickedListener,
    MailsAdapter.PersonClickListener, MailsAdapter.MailStarListner {

  @Inject IntentStarter intentStarter;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mails_base;
  }

  @Override protected ListAdapter<List<Mail>> createAdapter() {
    return new MailsAdapter(getActivity(), this, this, this);
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

    intentStarter.showMailDetails(getActivity(), mail, options.toBundle());
  }

  @Override public void onPersonClicked(Person person) {
    intentStarter.showProfile(getActivity(), person);
  }

  @Override public void onMailStarClicked(Mail mail) {
    presenter.starMail(mail, !mail.isStarred());
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

  @Override public void markMailAsRead(Mail mail, boolean read) {

    MailsAdapter.MailInAdapterResult result = ((MailsAdapter) adapter).findMail(mail);
    if (result.isFound()) {
      result.getAdapterMail().read(read);
      adapter.notifyDataSetChanged();
    }
  }
}
