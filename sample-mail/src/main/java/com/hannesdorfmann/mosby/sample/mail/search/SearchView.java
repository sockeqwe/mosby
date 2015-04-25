package com.hannesdorfmann.mosby.sample.mail.search;

import com.hannesdorfmann.mosby.sample.mail.base.view.BaseMailView;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface SearchView extends BaseMailView<List<Mail>> {

  public void addOlderMails(List<Mail> older);

  public void showLoadMore(boolean showLoadMore);

  public void showLoadMoreError(Throwable e);

  public void showSearchNotStartedYet();
}
