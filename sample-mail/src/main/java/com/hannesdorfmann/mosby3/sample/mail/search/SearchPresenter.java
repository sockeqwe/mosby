package com.hannesdorfmann.mosby3.sample.mail.search;

import android.text.TextUtils;
import com.hannesdorfmann.mosby3.sample.mail.base.presenter.BaseRxMailPresenter;
import com.hannesdorfmann.mosby3.sample.mail.model.event.LoginSuccessfulEvent;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailProvider;
import de.greenrobot.event.EventBus;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    older.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(olderSubscriber);
  }

  public void searchFor(String query, boolean pullToRefresh) {

    // If searching for empty string, then do nothing
    if (isViewAttached() && TextUtils.isEmpty(query)) {
      unsubscribe();
      getView().showSearchNotStartedYet();
      return;
    }

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

  public void onEventMainThread(LoginSuccessfulEvent event) {
    if (isViewAttached()) {
      getView().showSearchNotStartedYet();
    }
  }
}
