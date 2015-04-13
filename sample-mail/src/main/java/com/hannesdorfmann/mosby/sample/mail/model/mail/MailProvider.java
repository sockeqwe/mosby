/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.mail.model.mail;

import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.model.account.AccountManager;
import com.hannesdorfmann.mosby.sample.mail.model.account.NotAuthenticatedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
public class MailProvider {

  public static int DELAY = 2000;
  public static int authExceptionEach = 15;
  public static int errorEach = 5;
  private int counter = 0;

  private AccountManager accountManager;
  private List<Mail> mails;
  public static Label INBOX_LABEL = new Label(Label.INBOX, R.drawable.ic_inbox, 0);
  private Label sentLabel = new Label(Label.SENT, R.drawable.ic_send, 0);
  private Label spamLabel = new Label(Label.SPAM, R.drawable.ic_spam, 0);
  private Label trashLabel = new Label(Label.TRASH, R.drawable.ic_delete, 0);

  public MailProvider(AccountManager accountManager, MailGenerator genrator) {
    this.accountManager = accountManager;
    mails = genrator.generateMails();
  }

  /**
   * Get the labels with the number of unread mails.
   */
  public Observable<List<Label>> getLabels() {
    return Observable.just(mails).flatMap(new Func1<List<Mail>, Observable<List<Label>>>() {
      @Override public Observable<List<Label>> call(List<Mail> mails) {

        delay();

        Observable error = checkExceptions();
        if (error != null) {
          return error;
        }

        List<Label> labels = new ArrayList<>(4);
        labels.add(INBOX_LABEL);
        labels.add(sentLabel);
        labels.add(spamLabel);
        labels.add(trashLabel);

        int inbox = 0;
        int spam = 0;
        int sent = 0;
        int trash = 0;

        for (Mail m : mails) {

          if (m.isRead()) {
            continue;
          }

          switch (m.getLabel()) {
            case Label.INBOX:
              inbox++;
              break;

            case Label.SENT:
              sent++;
              break;

            case Label.SPAM:
              spam++;
              break;

            case Label.TRASH:
              trash++;
              break;
          }
        }

        INBOX_LABEL.setUnreadCount(inbox);
        sentLabel.setUnreadCount(sent);
        spamLabel.setUnreadCount(spam);
        trashLabel.setUnreadCount(trash);

        return Observable.just(labels);
      }
    });
  }

  public Observable<Mail> getMail(final int id) {
    return getFilteredMailList(new Func1<Mail, Boolean>() {
      @Override public Boolean call(Mail mail) {
        return mail.getId() == id;
      }
    }).flatMap(new Func1<List<Mail>, Observable<Mail>>() {
      @Override public Observable<Mail> call(List<Mail> mails) {

        if (mails == null || mails.isEmpty()) {
          return Observable.error(new NotFoundException());
        }

        return Observable.just(mails.get(0));
      }
    });
  }

  /**
   * Get a list of mails with the given label
   */
  public Observable<List<Mail>> getMailsOfLabel(final String l) {

    return getFilteredMailList(new Func1<Mail, Boolean>() {
      @Override public Boolean call(Mail mail) {
        return mail.getLabel().equals(l);
      }
    });
  }

  /**
   * Star or unstar a mail
   *
   * @param mailId the id of the mail
   * @param star true, if you want to star, false if you want to unstar
   */
  public Observable<Mail> starMail(int mailId, final boolean star) {

    return getMail(mailId).map(new Func1<Mail, Mail>() {
      @Override public Mail call(Mail mail) {
        mail.setStarred(star);
        return mail;
      }
    });
  }


  public Observable<Mail> setLabel(int mailId, final String label){
    return getMail(mailId).map(new Func1<Mail, Mail>() {
      @Override public Mail call(Mail mail) {
        mail.label(label);
        return mail;
      }
    });
  }

  /**
   * Filters the list of mails by the given criteria
   */
  private Observable<List<Mail>> getFilteredMailList(Func1<Mail, Boolean> filterFnc) {
    return Observable.defer(new Func0<Observable<Mail>>() {
      @Override public Observable<Mail> call() {

        delay();
        Observable o = checkExceptions();
        if (o != null) {
          return o;
        }

        return Observable.from(mails);
      }
    }).filter(filterFnc).collect(new Func0<List<Mail>>() {
      @Override public List<Mail> call() {
        return new ArrayList<Mail>();
      }
    }, new Action2<List<Mail>, Mail>() {
      @Override public void call(List<Mail> mails, Mail mail) {
        mails.add(mail);
      }
    }).map(new Func1<List<Mail>, List<Mail>>() {
      @Override public List<Mail> call(List<Mail> mails) {
        Collections.sort(mails, MailComparator.INSTANCE);
        return mails;
      }
    });
  }

  private void delay() {
    if (DELAY > 0) {

      try {
        Thread.sleep(DELAY);
      } catch (InterruptedException e) {
        // e.printStackTrace();
      }
    }
  }

  private Observable checkExceptions() {

    counter++;
    if (authExceptionEach > 0 && (!accountManager.isUserAuthenticated()
        || counter % authExceptionEach == 0)) {
      return Observable.error(new NotAuthenticatedException());
    }

    if (errorEach > 0 && counter % errorEach == 0) {
      return Observable.error(new Exception("Fake Excption"));
    }

    return null;
  }
}
