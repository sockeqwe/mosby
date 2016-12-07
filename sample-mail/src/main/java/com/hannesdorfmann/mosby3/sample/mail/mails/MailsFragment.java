package com.hannesdorfmann.mosby3.sample.mail.mails;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby3.sample.mail.IntentStarter;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseMailsFragment;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.melnykov.fab.FloatingActionButton;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Hannes Dorfmann
 */
public class MailsFragment extends BaseMailsFragment<MailsView, MailsPresenter>
    implements MailsView, MailsAdapter.MailClickedListener, MailsAdapter.MailStarListner {

  @Arg Label label;

  @Inject IntentStarter intentStarter;

  @Bind(R.id.createMail) FloatingActionButton createMailButton;

  MailsComponent mailsComponent;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_mails;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    createMailButton.attachToRecyclerView(recyclerView);
  }

  @Override public MailsPresenter createPresenter() {
    return mailsComponent.presenter();
  }

  @Override public void loadData(boolean b) {
    presenter.load(b, label);
  }

  @Override protected void injectDependencies() {
    mailsComponent = DaggerMailsComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build();
    mailsComponent.inject(this);
  }

  @Override public void changeLabel(Mail mail, String labelName) {

    MailsAdapter.MailInAdapterResult result = ((MailsAdapter) adapter).findMail(mail);
    if (result.isFound() && !labelName.equals(this.label.getName())) {
      // Found in adapter, but label has changed --> remove it
      adapter.getItems().remove(result.getIndex());
      adapter.notifyItemRemoved(result.getIndex());
      if (adapter.getItemCount() == 0) {
        emptyView.setVisibility(View.VISIBLE);
      }
    } else if (!result.isFound() && labelName.equals(this.label.getName())) {
      // Not found, but should be added
      adapter.getItems().add(result.getIndex(), mail);
      adapter.notifyItemInserted(result.getIndex());
      if (result.getIndex() == 0) {
        recyclerView.scrollToPosition(0);
      }

      if (adapter.getItemCount() > 0) {
        emptyView.setVisibility(View.GONE);
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

  @OnClick(R.id.createMail) public void onCreateMailClicked() {
    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), createMailButton,
            getString(R.string.shared_write_action));

    intentStarter.showWriteMail(getActivity(), null, options.toBundle());
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
}
