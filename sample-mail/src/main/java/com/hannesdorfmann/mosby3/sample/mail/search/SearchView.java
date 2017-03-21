package com.hannesdorfmann.mosby3.sample.mail.search;

import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseMailView;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface SearchView extends BaseMailView<List<Mail>> {

  void addOlderMails(List<Mail> older);

  void showLoadMore(boolean showLoadMore);

  void showLoadMoreError(Throwable e);

  void showSearchNotStartedYet();
}
