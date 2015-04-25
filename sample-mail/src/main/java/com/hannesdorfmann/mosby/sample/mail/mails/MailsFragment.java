package com.hannesdorfmann.mosby.sample.mail.mails;

import butterknife.InjectView;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.BaseMailsFragment;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.melnykov.fab.FloatingActionButton;

/**
 * @author Hannes Dorfmann
 */
public class MailsFragment
    extends BaseMailsFragment<MailsView, MailsPresenter>
    implements MailsView, MailsAdapter.MailClickedListener, MailsAdapter.MailStarListner {

  @Arg Label label;

  @InjectView(R.id.createMail) FloatingActionButton createMailButton;

  MailsComponent mailsComponent;

  @Override protected MailsPresenter createPresenter() {
    return mailsComponent.presenter();
  }

  @Override public void loadData(boolean b) {
    presenter.load(b, label);
  }

  @Override protected void injectDependencies() {
    mailsComponent = DaggerMailsComponent.create();
    mailsComponent.inject(this);
  }

  @Override public void changeLabel(Mail mail, String labelName) {

    MailsAdapter.MailInAdapterResult result = ((MailsAdapter) adapter).findMail(mail);
    if (result.isFound() && !labelName.equals(this.label.getName())) {
      // Found in adapter, but label has changed --> remove it
      adapter.getItems().remove(result.getIndex());
      adapter.notifyItemRemoved(result.getIndex());
    } else if (!result.isFound() && labelName.equals(this.label.getName())) {
      // Not found, but should be added
      adapter.getItems().add(result.getIndex(), mail);
      adapter.notifyItemInserted(result.getIndex());
      if (result.getIndex() == 0) {
        recyclerView.scrollToPosition(0);
      }
    }
  }
  
}
