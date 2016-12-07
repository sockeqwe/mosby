package com.hannesdorfmann.mosby3.sample.mail.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.BaseMailsFragment;
import com.hannesdorfmann.mosby3.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby3.sample.mail.base.view.viewstate.AuthViewState;
import com.hannesdorfmann.mosby3.sample.mail.dagger.NavigationModule;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.utils.BuildUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.Bind;

/**
 * @author Hannes Dorfmann
 */
public class SearchFragment extends BaseMailsFragment<SearchView, SearchPresenter>
    implements SearchView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.searchEditView) MaterialEditText searchEditView;

  private SearchComponent searchComponent;

  LinearLayoutManager layoutManager;
  boolean canLoadMore = true;
  boolean isLoadingMore = false;
  String lastQuery = "";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_search;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Init toolbar
    toolbar.setNavigationIcon(BuildUtils.getBackArrowDrawable(getActivity()));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        getActivity().finish();
      }
    });

    toolbar.inflateMenu(R.menu.search_menu);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.search) {
          loadData(false);
          return true;
        }
        return false;
      }
    });

    // search
    searchEditView.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        loadData(false);
      }

      @Override public void afterTextChanged(Editable s) {
      }
    });

    // load more
    layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (canLoadMore && !isLoadingMore && lastVisibleItemPosition == totalItemCount - 1) {
          loadOlderMails();
        }
      }
    });
  }

  @Override public void addOlderMails(List<Mail> older) {
    SearchResultAdapter searchAdapter = getAdapter();
    searchAdapter.addOlderMails(older);

    if (older.isEmpty()) {
      canLoadMore = false;
      Toast.makeText(getActivity(), R.string.search_no_more_mails_to_load, Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override public AuthViewState<List<Mail>, SearchView> createViewState() {
    return new SearchViewState();
  }

  @Override public SearchPresenter createPresenter() {
    return searchComponent.presenter();
  }

  private SearchResultAdapter getAdapter() {
    return (SearchResultAdapter) adapter;
  }

  @Override protected ListAdapter<List<Mail>> createAdapter() {
    return new SearchResultAdapter(getActivity(), this, this, this);
  }

  @Override public void showLoadMore(boolean showLoadMore) {
    getAdapter().setLoadMore(showLoadMore);
    SearchViewState searchViewState = (SearchViewState) getViewState();
    searchViewState.setLoadingMore(showLoadMore);
    isLoadingMore = showLoadMore;
  }

  @Override public void loadData(boolean pullToRefresh) {
    String query = getQueryString();

    // Workaround to solve textwatcher problem that forces reload on screen orientation change, since
    // textwatcher triggers onTextChanged() after restoring edittext after screen orientation changes
    if (pullToRefresh || !query.equals(lastQuery)) {
      presenter.searchFor(query, pullToRefresh);
      canLoadMore = true;
      lastQuery = query;
    }
  }

  private String getQueryString() {
    return searchEditView.getText().toString();
  }

  private void loadOlderMails() {
    Mail lastMail = getAdapter().getLastMailInList();
    if (lastMail != null) {
      presenter.loadOlderMails(getQueryString(), lastMail);
    }
  }

  @Override public void showSearchNotStartedYet() {
    loadingView.setVisibility(View.GONE);
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    authView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    lastQuery = "";

    SearchViewState vs = (SearchViewState) getViewState();
    vs.setShowSearchNotStarted();
  }

  @Override protected void onErrorViewClicked() {
    lastQuery = "";
    super.onErrorViewClicked();
  }

  @Override public void onNewViewStateInstance() {
    showSearchNotStartedYet();
  }

  @Override public void showLoadMoreError(Throwable e) {
    Toast.makeText(getActivity(), R.string.error_search_load_older, Toast.LENGTH_SHORT).show();
    lastQuery = "";
  }

  @Override protected void injectDependencies() {
    searchComponent = DaggerSearchComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .navigationModule(new NavigationModule())
        .build();


    searchComponent.inject(this);
  }
}
