package com.hannesdorfmann.mosby3.sample.mail.statistics;

import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxAuthPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.statistics.MailStatistics;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class StatisticsPresenter extends BaseRxAuthPresenter<StatisticsView, MailStatistics> {

  @Inject
  public StatisticsPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadStatistics() {

    subscribe(mailProvider.getStatistics(), false);
  }
}
