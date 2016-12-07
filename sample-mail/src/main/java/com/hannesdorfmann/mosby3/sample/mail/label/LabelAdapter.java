package com.hannesdorfmann.mosby3.sample.mail.label;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby3.sample.mail.R;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import com.hannesdorfmann.mosby3.sample.mail.utils.SimpleAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Hannes Dorfmann
 */
public class LabelAdapter extends SimpleAdapter<List<Label>> {

  static class ViewHolder {

    @Bind(R.id.name) TextView text;
    @Bind(R.id.icon) ImageView icon;

    public ViewHolder(View v) {
      ButterKnife.bind(this, v);
      icon.setColorFilter(icon.getResources().getColor(R.color.secondary_text));
    }
  }

  public LabelAdapter(Context context) {
    super(context);
  }

  @Override public View newView(int type, ViewGroup parent) {
    View view = inflater.inflate(R.layout.list_labelview_item, parent, false);
    view.setTag(new ViewHolder(view));
    return view;
  }

  @Override public void bindView(int position, int type, View view) {
    ViewHolder vh = (ViewHolder) view.getTag();
    Label label = items.get(position);

    vh.text.setText(label.getName());
    vh.icon.setImageResource(label.getIconRes());
  }
}
