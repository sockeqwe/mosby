package com.hannesdorfmann.mosby3.sample.mail.search;

import android.content.Context;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.mails.MailsAdapter;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class SearchResultAdapter extends MailsAdapter implements SearchResultAdapterBinder {

  @ViewType(
      layout = R.layout.list_load_more) public final int loadMore = 1;

  private boolean showLoadMore = false;

  public SearchResultAdapter(Context context, MailClickedListener clickListener,
      MailStarListner starListener, PersonClickListener personClickListener) {
    super(context, clickListener, starListener, personClickListener);
  }

  @Override public int getItemCount() {
    return super.getItemCount() + (showLoadMore ? 1 : 0);
  }

  @Override public int getItemViewType(int position) {

    if (showLoadMore && position == items.size()) { // At last position add one
      return loadMore;
    }

    return super.getItemViewType(position);
  }

  public void setLoadMore(boolean enabled) {

    if (showLoadMore != enabled) {

      if (showLoadMore) {
        showLoadMore = false;
        notifyItemRemoved(items.size()); // Remove last position
      } else {
        showLoadMore = true;
        notifyItemInserted(items.size());
      }
    }
  }

  public void addOlderMails(List<Mail> olderMails) {
    if (!olderMails.isEmpty()) {
      int startPosition = items.size();
      items.addAll(olderMails);
      notifyItemRangeInserted(startPosition, olderMails.size());
    }
  }

  @Override
  public void bindViewHolder(SearchResultAdapterHolders.LoadMoreViewHolder vh, int position) {
    // Nothing to bind
  }

  public Mail getLastMailInList() {
    return items == null ? null : items.get(items.size() - 1);
  }
}
