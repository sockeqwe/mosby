package com.hannesdorfmann.mosby.sample.mail.label;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class LabelPresenter extends MvpLceRxPresenter<LabelView, List<Label>> {

  private EventBus eventBus;
  private MailProvider mailProvider;

  @Inject public LabelPresenter(EventBus eventBus, MailProvider mailProvider) {
    this.eventBus = eventBus;
    this.mailProvider = mailProvider;
  }

  public void loadLabels() {
    subscribe(mailProvider.getLabels(), false);
  }
}
