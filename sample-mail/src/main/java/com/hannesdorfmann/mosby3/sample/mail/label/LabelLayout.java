package com.hannesdorfmann.mosby3.sample.mail.label;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.layout.MvpViewStateLinearLayout;
import com.hannesdorfmann.mosby3.sample.mail.MailApplication;
import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Mail;
import com.hannesdorfmann.mosby3.sample.mail.utils.DimensUtils;

import java.util.List;

import butterknife.Bind;
import icepick.Icepick;
import icepick.Icicle;

/**
 * @author Hannes Dorfmann
 */
public class LabelLayout extends MvpViewStateLinearLayout<LabelView, LabelPresenter>
    implements LabelView {

  @Bind(R.id.labelTextView) TextView labelView;
  @Bind(R.id.labelLoadingView) View loadingView;
  @Icicle Mail mail;

  ListPopupWindow popUpWindow;
  LabelAdapter adapter;

  public LabelLayout(Context context) {
    super(context);
    init();
  }

  public LabelLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this, this);
  }

  private void init() {

    View.inflate(getContext(), R.layout.view_label_layout, this);

    LayoutTransition transition = new LayoutTransition();
    transition.enableTransitionType(LayoutTransition.CHANGING);
    this.setLayoutTransition(transition);

    adapter = new LabelAdapter(getContext());
    popUpWindow = new ListPopupWindow(getContext());
    popUpWindow.setAnchorView(this);
    popUpWindow.setAdapter(adapter);
    popUpWindow.setWidth(DimensUtils.dpToPx(getContext(), 140));
    popUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override public void onDismiss() {
        showLabel();
      }
    });
    popUpWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Label label = (Label) adapter.getItem(position);
        if (!label.getName().equals(mail.getLabel())) {
          presenter.setLabel(mail, label.getName());
          popUpWindow.dismiss();
        }
      }
    });

    setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        loadData(false);
      }
    });
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    popUpWindow.setOnDismissListener(null);

    if (popUpWindow.isShowing()) {
      popUpWindow.dismiss();
    }
  }

  public void setMail(Mail mail) {
    this.mail = mail;
    labelView.setText(mail.getLabel());
  }


  @Override public LabelPresenter createPresenter() {
    return DaggerLabelLayoutComponent.builder()
        .mailAppComponent(MailApplication.getMailComponents())
        .build()
        .presenter();
  }

  @Override public void showLoading(boolean pullToRefresh) {
    loadingView.setVisibility(View.VISIBLE);
    this.setClickable(false);
    getViewState().setStateShowLoading(false);
  }

  @Override public void showContent() {
    loadingView.setVisibility(View.GONE);
    this.setClickable(true);
    post(new Runnable() {
      @Override public void run() { // Need for rotation changes
        popUpWindow.show();
      }
    });
    getViewState().setStateShowContent(adapter.getItems());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    if (!isRestoringViewState()) {
      Toast.makeText(getContext(), R.string.error_loading_labels, Toast.LENGTH_SHORT).show();
    }
    showLabel();
  }

  @Override public void setData(List<Label> data) {
    adapter.setItems(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadLabels();
  }

  @Override public void showLabel() {
    if (popUpWindow.isShowing()) {
      popUpWindow.dismiss();
    }

    loadingView.setVisibility(View.GONE);
    this.setClickable(true);
    getViewState().setStateShowingLabel();
  }

  @Override public ViewState createViewState() {
    return new LabelViewState();
  }

  @Override public LabelViewState getViewState() {
    return (LabelViewState) super.getViewState();
  }

  @Override public void onNewViewStateInstance() {
    showLabel();
  }

  @Override public Parcelable onSaveInstanceState() {
    return Icepick.saveInstanceState(this, super.onSaveInstanceState());
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
  }

  @Override public void changeLabel(Mail m, String label) {
    if (m.getId() == this.mail.getId()) {
      mail.label(label);
      labelView.setText(label);
    }
  }

  @Override public void showChangeLabelFailed(Mail mail, Throwable t) {
    Toast.makeText(getContext(), R.string.error_label_change_failed, Toast.LENGTH_SHORT).show();
  }
}
