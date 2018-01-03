package com.hannesdorfmann.mosby3.sample.mail.statistics;

import android.content.Context;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.ViewField;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.statistics.MailsCount;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class StatisticsAdapter extends ListAdapter<List<MailsCount>>
    implements StatisticsAdapterBinder {

  @ViewType(
      layout = R.layout.list_statistics,
      views = @ViewField(id = R.id.text, name = "text", type = TextView.class)) public final int
      statisticItem = 0;

  public StatisticsAdapter(Context context) {
    super(context);
  }

  @Override
  public void bindViewHolder(StatisticsAdapterHolders.StatisticItemViewHolder vh, int position) {
    MailsCount count = items.get(position);
    vh.text.setText(count.getMailsCount() + " mails in " + count.getLabel());
  }
}
