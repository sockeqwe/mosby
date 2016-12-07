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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
class TestMailGenerator implements MailGenerator {

  public static final int MAILS_PER_LABEL = 10;
  public static final String TEXT = "Text";
  public static final String SUBJECT = "Text";
  public static final Date DATE = new Date();


  @Override public List<Mail> generateMails() {

    List<Mail> mailsList = new ArrayList<>();


    String[] labels = {Label.INBOX, Label.SENT, Label.SPAM, Label.TRASH};

    int id = 0;

    for (String label : labels){
      for (int i = 0; i< MAILS_PER_LABEL; i++){
        mailsList.add(new Mail().id(id++).text(TEXT).subject(SUBJECT).date(DATE).label(label).read(i%2 == 0));
      }
    }

    return mailsList;
  }

  @Override public Mail generateResponseMail(String senderMail) {
    return null;
  }
}
