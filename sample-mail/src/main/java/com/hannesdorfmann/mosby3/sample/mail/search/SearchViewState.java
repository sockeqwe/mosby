package com.hannesdorfmann.mosby3.sample.mail.search;

import android.os.Parcel;
import com.hannesdorfmann.mosby3.sample.mail.base.view.viewstate.AuthCastedArrayListViewState;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class SearchViewState extends AuthCastedArrayListViewState<List<Mail>, SearchView> {

  public static final Creator<SearchViewState> CREATOR = new Creator<SearchViewState>() {
    @Override public SearchViewState createFromParcel(Parcel source) {
      return new SearchViewState(source);
    }

    @Override public SearchViewState[] newArray(int size) {
      return new SearchViewState[size];
    }
  };

  /**
   * This will be the initial state. Not searched for mails yet, therefore not show an error and
   * not
   * show loading and not show content
   */
  public static final int STATE_SHOW_SEARCH_NOT_STARTED = 5;

  boolean loadingMore = false;

  public SearchViewState() {
  }

  protected SearchViewState(Parcel source) {
    super(source);
  }

  public void setLoadingMore(boolean loadingMore) {
    this.loadingMore = loadingMore;
  }

  public void setShowSearchNotStarted() {
    currentViewState = STATE_SHOW_SEARCH_NOT_STARTED;
    loadedData = null;
    exception = null;
  }

  @Override public void apply(SearchView view, boolean retained) {
    if (currentViewState == STATE_SHOW_SEARCH_NOT_STARTED) {
      view.showSearchNotStartedYet();
    } else {

      super.apply(view, retained);

      if (currentViewState == STATE_SHOW_CONTENT) {
        view.showLoadMore(loadingMore);
      }
    }
  }
}
