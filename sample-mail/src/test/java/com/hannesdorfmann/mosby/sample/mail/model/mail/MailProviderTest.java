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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Subscriber;

/**
 * @author Hannes Dorfmann
 */
public class MailProviderTest {

  private static MailProvider mailProvider;

  @BeforeClass public static void init() {
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

  @Test
  public void getMailById(){

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
        Assert.fail("A mail with id = " + id+" found, but a mail with this id should not exist");
      }
    });

    Assert.assertTrue(errorThrown.get());
  }
}
