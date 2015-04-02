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
import android.widget.ImageView;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.Field;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.annotatedadapter.support.recyclerview.SupportAnnotatedAdapter;
import com.hannesdorfmann.mosby.sample.dagger1.R;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

/**
 * @author Hannes Dorfmann
 */
public class MembersAdapter extends SupportAnnotatedAdapter {

  @ViewType(
      layout = R.layout.list_member,
      fields = {
          @Field(id = R.id.avatar, type = ImageView.class, name = "avatar"),
          @Field(id = R.id.username, type = TextView.class, name = "username")

      }
  )
  public final int member = 0;


  private Picasso picasso;

  @Inject
  public MembersAdapter(Context context, Picasso picasso) {
    super(context);
  }



}
