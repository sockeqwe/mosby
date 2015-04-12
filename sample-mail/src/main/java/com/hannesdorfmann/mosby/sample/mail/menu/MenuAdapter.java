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

package com.hannesdorfmann.mosby.sample.mail.menu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.Field;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.base.view.ListAdapter;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class MenuAdapter extends ListAdapter<List<Label>> implements MenuAdapterBinder {

  public interface LabelClickListener{
      public void onLabelClicked(Label label);
  }


  @ViewType(layout = R.layout.list_menu_item,
      initMethod = true,
      fields = {
          @Field(id = R.id.icon, name = "icon", type = ImageView.class),
          @Field(id = R.id.name, name = "name", type = TextView.class),
          @Field(id = R.id.unreadCount, name = "unread", type = TextView.class)
      }) public final int menuItem = 0;

  private Context context;
  private LabelClickListener listener;


  public MenuAdapter(Context context, LabelClickListener listener) {
    super(context);
    this.context = context;
    this.listener = listener;
  }

  @Override public void initViewHolder(MenuAdapterHolders.MenuItemViewHolder vh, View view,
      ViewGroup parent) {
    vh.icon.setColorFilter(context.getResources().getColor(R.color.secondary_text));
  }

  @Override public void bindViewHolder(MenuAdapterHolders.MenuItemViewHolder vh, int position) {
    final Label label = items.get(position);
    vh.icon.setImageResource(label.getIconRes());
    vh.name.setText(label.getName());

    if (label.getUnreadCount() > 0) {
      vh.unread.setVisibility(View.VISIBLE);
      vh.unread.setText("" + label.getUnreadCount());
    } else {
      vh.unread.setVisibility(View.INVISIBLE);
    }

    vh.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onLabelClicked(label);
      }
    });
  }
}
