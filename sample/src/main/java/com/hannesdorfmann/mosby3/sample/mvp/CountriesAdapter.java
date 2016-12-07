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

package com.hannesdorfmann.mosby3.sample.mvp;

import android.content.Context;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.ViewField;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.annotatedadapter.support.recyclerview.SupportAnnotatedAdapter;
import com.hannesdorfmann.mosby3.sample.R;
import com.hannesdorfmann.mosby3.sample.mvp.model.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesAdapter extends SupportAnnotatedAdapter implements CountriesAdapterBinder {

  @ViewType(
      layout = R.layout.row_text,
      views = {
          @ViewField(
              id = R.id.textView,
              name = "name",
              type = TextView.class)
      }) public final int VIEWTYPE_COUNTRY = 0;

  private List<Country> countries;

  public CountriesAdapter(Context context) {
    super(context);
  }

  public void setCountries(List<Country> countries) {
    this.countries = countries;
  }

  public List<Country> getCountries() {
    return countries;
  }

  @Override public int getItemCount() {
    return countries == null ? 0 : countries.size();
  }

  @Override
  public void bindViewHolder(CountriesAdapterHolders.VIEWTYPE_COUNTRYViewHolder vh, int position) {
    vh.name.setText(countries.get(position).getName());
  }
}


