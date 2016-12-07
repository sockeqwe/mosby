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

package com.hannesdorfmann.mosby3.sample.mail.mails;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.ViewField;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.MailComparator;
import com.hannesdorfmann.mosby3.sample.mail.ui.view.StarView;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Hannes Dorfmann
 */
public class MailsAdapter extends ListAdapter<List<Mail>> implements MailsAdapterBinder {

  public interface MailClickedListener {
    public void onMailClicked(MailsAdapterHolders.MailViewHolder vh, Mail mail);
  }

  public interface MailStarListner {
    public void onMailStarClicked(Mail mail);
  }

  public interface PersonClickListener {
    public void onPersonClicked(Person person);
  }

  @ViewType(
      layout = R.layout.list_mail_item,
      views = {
          @ViewField(id = R.id.senderPic, name = "senderPic", type = ImageView.class),
          @ViewField(id = R.id.subject, name = "subject", type = TextView.class),
          @ViewField(id = R.id.message, name = "message", type = TextView.class),
          @ViewField(id = R.id.date, name = "date", type = TextView.class),
          @ViewField(id = R.id.starButton, name = "star", type = StarView.class)
      }) public final int mail = 0;

  private MailClickedListener clickListener;
  private MailStarListner starListner;
  private PersonClickListener personClickListener;
  private Format format = new SimpleDateFormat("dd. MMM", Locale.getDefault());

  public MailsAdapter(Context context, MailClickedListener clickListener,
      MailStarListner starListener, PersonClickListener personClickListener) {
    super(context);
    this.clickListener = clickListener;
    this.starListner = starListener;
    this.personClickListener = personClickListener;
  }

  @Override public void bindViewHolder(final MailsAdapterHolders.MailViewHolder vh, int position) {
    final Mail mail = items.get(position);

    vh.senderPic.setImageResource(mail.getSender().getImageRes());
    vh.subject.setText(mail.getSubject());
    vh.message.setText(
        Html.fromHtml(mail.getSender().getName() + " - <i>" + mail.getText() + "</i>"));
    vh.date.setText(format.format(mail.getDate()));
    vh.star.setStarred(mail.isStarred());
    vh.star.clearAnimation();

    if (mail.isRead()) {
      vh.subject.setTypeface(null, Typeface.NORMAL);
      vh.message.setTypeface(null, Typeface.NORMAL);
      vh.date.setTypeface(null, Typeface.NORMAL);
    } else {
      vh.subject.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
      vh.message.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
      vh.date.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
    }

    vh.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        clickListener.onMailClicked(vh, mail);
      }
    });
    vh.star.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        starListner.onMailStarClicked(mail);
      }
    });
    vh.senderPic.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        personClickListener.onPersonClicked(mail.getSender());
      }
    });
  }

  /**
   * Finds a mail by his id if displayed in this adapter
   */
  public Mail findMail(int id) {
    if (items == null) {
      return null;
    }

    for (Mail m : items) {
      if (m.getId() == id) {
        return m;
      }
    }

    return null;
  }

  /**
   * Searches for an equal mail (compares mail id) in the adapter.
   *
   * @return A {@link MailInAdapterResult} containing the information if the adapter contains that
   * mail and at which index position. If the adapter doesn't contain this mail, then the result
   * will contain the index position where the mail would be.
   */
  public MailInAdapterResult findMail(Mail mail) {
    int indexPosition = Collections.binarySearch(items, mail, MailComparator.INSTANCE);
    boolean containsMail = false;
    Mail found = null;
    if (indexPosition < 0) {
      indexPosition = ~indexPosition;
    } else {
      found = items.get(indexPosition);
      if (found.getId() == mail.getId()) {
        containsMail = true;
      } else {
        containsMail = false;
        found = null;
      }
    }

    return new MailInAdapterResult(containsMail, found, indexPosition);
  }

  /**
   * Holds the information if the adapter contains a certain mail and at which index position. If
   * the adapter doesn't contain this mail, then the result will
   * contain the index position where the mail would be.
   */
  public static class MailInAdapterResult {
    boolean found;
    Mail adapterMail;
    int index;

    public MailInAdapterResult(boolean found, Mail adapterMail, int index) {
      this.found = found;
      this.adapterMail = adapterMail;
      this.index = index;
    }

    public boolean isFound() {
      return found;
    }

    public Mail getAdapterMail() {
      return adapterMail;
    }

    public int getIndex() {
      return index;
    }
  }
}
