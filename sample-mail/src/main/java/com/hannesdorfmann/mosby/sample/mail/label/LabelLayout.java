package com.hannesdorfmann.mosby.sample.mail.label;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateLinearLayout;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby.sample.mail.utils.DimensUtils;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class LabelLayout extends MvpViewStateLinearLayout<LabelPresenter> implements LabelView {

  @InjectView(R.id.labelTextView) TextView labelView;
  @InjectView(R.id.labelLoadingView) View loadingView;
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

  public void setLabel(String label) {
    labelView.setText(label);
  }

  @Override protected LabelPresenter createPresenter() {
    return DaggerLabelLayoutComponent.create().presenter();
  }

  @Override public void showLoading(boolean pullToRefresh) {
    loadingView.setVisibility(View.VISIBLE);
    this.setClickable(false);
    getViewState().setStateShowLoading(false);
  }

  @Override public void showContent() {
    loadingView.setVisibility(View.GONE);
    this.setClickable(true);
    popUpWindow.show();
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
}
