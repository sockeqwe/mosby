package com.hannesdorfmann.mosby3.sample.mail.statistics;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.mvp.delegate.FragmentMvpDelegate;
import com.hannesdorfmann.mosby3.mvp.delegate.FragmentMvpViewStateDelegateImpl;
import com.hannesdorfmann.mosby3.mvp.delegate.MvpViewStateDelegateCallback;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.base.view.viewstate.AuthParcelableDataViewState;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.statistics.MailStatistics;

/**
 * @author Hannes Dorfmann
 */
public class StatisticsDialog extends AppCompatDialogFragment implements StatisticsView,
    MvpViewStateDelegateCallback<StatisticsView, StatisticsPresenter> {

  @Bind(R.id.contentView) RecyclerView contentView;
  @Bind(R.id.loadingView) View loadingView;
  @Bind(R.id.errorView) TextView errorView;
  @Bind(R.id.authView) View authView;

  StatisticsPresenter presenter;
  ViewState<StatisticsView> viewState;
  MailStatistics data;
  StatisticsAdapter adapter;

  private FragmentMvpDelegate<StatisticsView, StatisticsPresenter> delegate =
      new FragmentMvpViewStateDelegateImpl<>(this);

  //
  // DELEGATE callback
  //

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    delegate.onCreate(savedInstanceState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    delegate.onDestroy();
  }

  @Override public void onPause() {
    super.onPause();
    delegate.onPause();
  }

  @Override public void onResume() {
    super.onResume();
    delegate.onResume();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_statistics, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    delegate.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);
    adapter = new StatisticsAdapter(getActivity());
    contentView.setAdapter(adapter);
    contentView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }

  @Override public void onStart() {
    super.onStart();
    delegate.onStart();
  }

  @Override public void onStop() {
    super.onStop();
    delegate.onStop();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    delegate.onAttach(activity);
  }

  @Override public void onDetach() {
    super.onDetach();
    delegate.onDetach();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    delegate.onActivityCreated(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    delegate.onSaveInstanceState(outState);
  }

  //
  // End delegates
  //

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    AppCompatDialog dialog = new AppCompatDialog(getActivity(), getTheme());
    dialog.setTitle(R.string.menu_statistics);
    return dialog;
  }

  @Override public void showAuthenticationRequired() {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    authView.setVisibility(View.VISIBLE);

    ((AuthParcelableDataViewState<MailStatistics, StatisticsView>) viewState).setShowingAuthenticationRequired();
  }

  @Override public void showLoading(boolean pullToRefresh) {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.VISIBLE);
    authView.setVisibility(View.GONE);

    ((AuthParcelableDataViewState<MailStatistics, StatisticsView>) viewState).setStateShowLoading(
        pullToRefresh);
  }

  @Override public void showContent() {
    contentView.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.GONE);
    authView.setVisibility(View.GONE);

    ((AuthParcelableDataViewState<MailStatistics, StatisticsView>) viewState).setStateShowContent(
        data);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.VISIBLE);
    loadingView.setVisibility(View.GONE);
    authView.setVisibility(View.GONE);
    ((AuthParcelableDataViewState<MailStatistics, StatisticsView>) viewState).setStateShowError(e,
        pullToRefresh);
  }

  @Override public void setData(MailStatistics data) {
    this.data = data;
    adapter.setItems(data.getMailsCounts());
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadStatistics();
  }

  @Override public ViewState<StatisticsView> getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState<StatisticsView> viewState) {
    this.viewState = viewState;
  }

  @Override public ViewState<StatisticsView> createViewState() {
    return new AuthParcelableDataViewState<MailStatistics, StatisticsView>();
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    // Not needed
  }

  @Override public boolean isRestoringViewState() {
    // Not needed
    return false;
  }

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // Not needed
  }

  @Override public void onNewViewStateInstance() {
    loadData(false);
  }

  @Override public StatisticsPresenter createPresenter() {
    return DaggerStatisticsComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .build()
        .presenter();
  }

  @Override public StatisticsPresenter getPresenter() {
    return presenter;
  }

  @Override public void setPresenter(StatisticsPresenter presenter) {
    this.presenter = presenter;
  }

  @Override public StatisticsView getMvpView() {
    return this;
  }

  @Override public boolean isRetainInstance() {
    return getRetainInstance();
  }

  @Override public boolean shouldInstanceBeRetained() {
    FragmentActivity activity = getActivity();
    boolean changingConfig = activity != null && activity.isChangingConfigurations();
    return getRetainInstance() && changingConfig;
  }
}
