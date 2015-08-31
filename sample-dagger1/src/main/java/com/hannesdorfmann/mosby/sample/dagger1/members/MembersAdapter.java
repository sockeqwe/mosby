/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.dagger1.members;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.ViewField;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.annotatedadapter.support.recyclerview.SupportAnnotatedAdapter;
import com.hannesdorfmann.mosby.sample.dagger1.R;
import com.hannesdorfmann.mosby.sample.dagger1.model.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MembersAdapter
    extends SupportAnnotatedAdapter implements MembersAdapterBinder {

  @ViewType(
      layout = R.layout.list_member,
      views = {
          @ViewField(id = R.id.avatar, type = ImageView.class, name = "avatar"),
          @ViewField(id = R.id.username, type = TextView.class, name = "username")

      }
  )
  public final int member = 0;


  private List<User> members;
  private Picasso picasso;
  private Transformation roundedTransformation;

  @Inject
  public MembersAdapter(Context context, Picasso picasso) {
    super(context);

    this.picasso = picasso;

    roundedTransformation = new RoundedTransformationBuilder()
        .borderColor(Color.BLACK)
        .borderWidthDp(1)
        .oval(false).build();

  }

  @Override public int getItemCount() {
    return members == null ? 0 : members.size();
  }

  public List<User> getMembers() {
    return members;
  }

  public void setMembers(List<User> members) {
    this.members = members;
  }

  @Override public void bindViewHolder(MembersAdapterHolders.MemberViewHolder vh, int position) {
    User member = members.get(position);

    vh.username.setText(member.getLogin());

    picasso.load(member.getAvatarUrl()).fit().transform(roundedTransformation).into(vh.avatar);
  }
}

