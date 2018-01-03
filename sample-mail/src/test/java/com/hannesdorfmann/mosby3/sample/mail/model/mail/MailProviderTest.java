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

package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * @author Hannes Dorfmann
 */
public class MailProviderTest {

  private MailProvider mailProvider;

  @Before public void init() {
    mailProvider = new MailProvider(new TestAccountManager(), new TestMailGenerator());
    mailProvider.DELAY = 0;
    mailProvider.authExceptionEach = 0;
    mailProvider.errorEach = 0;
  }

  @Test public void getMailsByLabel() {

    String[] labels = { Label.INBOX, Label.SENT, Label.TRASH, Label.SPAM };

    for (String label : labels) {
      final AtomicBoolean sucessful = new AtomicBoolean(false);
      mailProvider.getMailsOfLabel(label).subscribe(new Subscriber<List<Mail>>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable e) {
          e.printStackTrace();
          Assert.fail("excption thrown" + e.getMessage());
        }

        @Override public void onNext(List<Mail> mails) {
          Assert.assertEquals(TestMailGenerator.MAILS_PER_LABEL, mails.size());
          sucessful.set(true);
        }
      });

      Assert.assertTrue(sucessful.get());
    }
  }

  @Test public void getMailById() {

    final AtomicBoolean mailFound = new AtomicBoolean(false);
    final int id = 1;
    mailProvider.getMail(id).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        Assert.fail("Could not find mail with the id = " + id);
      }

      @Override public void onNext(Mail mail) {
        Assert.assertEquals(id, mail.getId());
        mailFound.set(true);
      }
    });

    Assert.assertTrue(mailFound.get());

    // id not exists

    final AtomicBoolean errorThrown = new AtomicBoolean(false);
    mailProvider.getMail(-1).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        Assert.assertTrue(e instanceof NotFoundException);
        errorThrown.set(true);
      }

      @Override public void onNext(Mail mail) {
        Assert.fail("A mail with id = " + id + " found, but a mail with this id should not exist");
      }
    });

    Assert.assertTrue(errorThrown.get());
  }

  @Test public void starMail() {
    final AtomicBoolean starredMail = new AtomicBoolean(false);
    final int id = 1;
    mailProvider.starMail(id, true).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        e.printStackTrace();
        Assert.fail("Error while starring mail with id = " + id);
      }

      @Override public void onNext(Mail mail) {
        Assert.assertEquals(id, mail.getId());
        Assert.assertTrue(mail.isStarred());
        starredMail.set(true);
      }
    });

    Assert.assertTrue(starredMail.get());

    final AtomicBoolean errorThrown = new AtomicBoolean(false);
    mailProvider.starMail(-1, true).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        Assert.assertTrue(e instanceof NotFoundException);
        errorThrown.set(true);
      }

      @Override public void onNext(Mail mail) {
        Assert.fail("A mail with id = "
            + id
            + " has been starred, but a mail with this id should not exist");
      }
    });

    Assert.assertTrue(errorThrown.get());

    //
    // Unstar
    //
    final AtomicBoolean unStaredMail = new AtomicBoolean(false);
    mailProvider.starMail(id, false).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        e.printStackTrace();
        Assert.fail("Error while unstaring mail with id = " + id);
      }

      @Override public void onNext(Mail mail) {
        Assert.assertEquals(id, mail.getId());
        Assert.assertFalse(mail.isStarred());
        unStaredMail.set(true);
      }
    });

    Assert.assertTrue(unStaredMail.get());

    final AtomicBoolean unstarErrorThrown = new AtomicBoolean(false);
    mailProvider.getMail(-1).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        Assert.assertTrue(e instanceof NotFoundException);
        unstarErrorThrown.set(true);
      }

      @Override public void onNext(Mail mail) {
        Assert.fail("A mail with id = "
            + id
            + " has been unstarred, but a mail with this id should not exist");
      }
    });

    Assert.assertTrue(unstarErrorThrown.get());
  }

  @Test public void changeLabel() {
    mailProvider.getMail(1).subscribe(new Action1<Mail>() {
      @Override public void call(Mail mail) {

        final AtomicBoolean changed = new AtomicBoolean(false);
        final String newLabel = "foo";
        mailProvider.setLabel(mail.getId(), newLabel).subscribe(new Subscriber<Mail>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            Assert.fail("Error while changing label");
          }

          @Override public void onNext(Mail mail) {
            Assert.assertEquals(mail.getLabel(), newLabel);
            changed.set(true);
          }
        });

        Assert.assertTrue(changed.get());
      }
    });
  }

  @Test public void createMail() {
    final Mail mail = new Mail();
    mail.id(-1);

    final int id = mailProvider.getLastMailId();
    final AtomicBoolean successCalled = new AtomicBoolean(false);

    mailProvider.addMailWithDelay(mail).subscribe(new Subscriber<Mail>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        e.printStackTrace();
        Assert.fail("error occcurred");
      }

      @Override public void onNext(Mail created) {
        Assert.assertEquals(mail, created);
        Assert.assertEquals(created.getId(), id + 1);

        successCalled.set(true);

        mailProvider.getMail(id).subscribe(new Action1<Mail>() {
          @Override public void call(Mail queried) {

            Assert.assertEquals(queried.getId(), id);
          }
        });
      }
    });

    Assert.assertTrue(successCalled.get());
  }
}
