package com.hannesdorfmann.mosby.sample.mail.search;

import com.hannesdorfmann.mosby.mvp.rx.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.mosby.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby.sample.mail.model.mail.MailProvider;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class SearchPresenter extends BaseRxMailPresenter<SearchView, List<Mail>> {

  private int queryLimit = 20;
  private Subscriber<List<Mail>> olderSubscriber;

  @Inject public SearchPresenter(MailProvider mailProvider, EventBus eventBus) {
    super(mailProvider, eventBus);
  }

  public void loadOlderMails(String query, Mail olderAs) {

    // Cancel any previous query
    unsubscribe();

    final Observable<List<Mail>> older =
        mailProvider.searchForOlderMails(query, olderAs.getDate(), queryLimit);

    if (isViewAttached()) {
      getView().showLoadMore(true);
    }
    olderSubscriber = new Subscriber<List<Mail>>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(Throwable e) {
        if (isViewAttached()) {
          getView().showLoadMoreError(e);
          getView().showLoadMore(false);
        }
      }

      @Override public void onNext(List<Mail> mails) {
        if (isViewAttached()) {
          getView().addOlderMails(mails);
          getView().showLoadMore(false);
        }
      }
    };

    // start
    older.compose(new AndroidSchedulerTransformer<List<Mail>>()).subscribe(olderSubscriber);
  }

  public void searchFor(String query, boolean pullToRefresh) {

    // in case the previous action was load more we have to reset the view
    if (isViewAttached()) {
      getView().showLoadMore(false);
    }

    subscribe(mailProvider.searchForMails(query, queryLimit), pullToRefresh);
  }

  @Override protected void unsubscribe() {
    super.unsubscribe();
    if (olderSubscriber != null && !olderSubscriber.isUnsubscribed()) {
      olderSubscriber.unsubscribe();
    }
  }

  @Override protected void onError(Throwable e, boolean pullToRefresh) {
    super.onError(e, pullToRefresh);
  }

  @Override protected void onNext(List<Mail> data) {
    super.onNext(data);
  }

  @Override protected void onCompleted() {
    super.onCompleted();
  }
}
