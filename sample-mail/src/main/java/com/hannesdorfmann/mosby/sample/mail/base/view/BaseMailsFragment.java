package com.hannesdorfmann.mosby.sample.mail.base.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby.sample.mail.mails.MailsAdapter;
import com.hannesdorfmann.mosby.sample.mail.mails.MailsAdapterHolders;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import com.melnykov.fab.FloatingActionButton;
import java.util.List;

/**
 * Base class for fragments that want to dipslay a list of Mails
 *
 * @author Hannes Dorfmann
 */
public abstract class BaseMailsFragment<V extends BaseMailView<List<Mail>>, P extends BaseRxMailPresenter<V, List<Mail>>>
    extends AuthRefreshRecyclerFragment<List<Mail>, V, P>
    implements BaseMailView<List<Mail>>, MailsAdapter.MailClickedListener, MailsAdapter.PersonClickListener,
    MailsAdapter.MailStarListner {

  @InjectView(R.id.createMail) FloatingActionButton createMailButton;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mails;
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

    IntentStarter.showMailDetails(getActivity(), mail, options.toBundle());
  }

  @Override public void onPersonClicked(Person person) {
    // TODO implement
    IntentStarter.showProfile(getActivity(), person);
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

  @Override public void showContent() {
    super.showContent();
    if (createMailButton.getVisibility() != View.VISIBLE) {
      createMailButton.setVisibility(View.VISIBLE);

      if (!isRestoringViewState()) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        ObjectAnimator animator =
            ObjectAnimator.ofPropertyValuesHolder(createMailButton, holderX, holderY);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
      }
    }
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    if (!pullToRefresh) {
      createMailButton.setVisibility(View.GONE);
    }
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    if (!pullToRefresh) {
      createMailButton.setVisibility(View.GONE);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    createMailButton.attachToRecyclerView(recyclerView);
  }

  @OnClick(R.id.createMail) public void onCreateMailClicked() {
    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), createMailButton,
            getString(R.string.shared_write_action));

    IntentStarter.showWriteMail(getActivity(), null, options.toBundle());
  }

  @Override public void markMailAsRead(Mail mail, boolean read) {

    MailsAdapter.MailInAdapterResult result = ((MailsAdapter) adapter).findMail(mail);
    if (result.isFound()) {
      result.getAdapterMail().read(read);
      adapter.notifyDataSetChanged();
    }
  }


}
