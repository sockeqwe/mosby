package com.hannesdorfmann.mosby.sample.mvp.lce;

import android.content.Context;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.Field;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.annotatedadapter.support.recyclerview.SupportAnnotatedAdapter;
import com.hannesdorfmann.mosby.sample.R;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesAdapter extends SupportAnnotatedAdapter implements CountriesAdapterBinder {

  @ViewType(
      layout = R.layout.row_text,
      fields = {
          @Field(
              id = R.id.textView,
              name = "name",
              type = TextView.class)
      })
  public final int VIEWTYPE_COUNTRY = 0;

  private List<Country> countries;

  public CountriesAdapter(Context context) {
    super(context);
  }

  public void setCountries(List<Country> countries) {
    this.countries = countries;
  }

  @Override public int getItemCount() {
    return countries == null ? 0 : countries.size();
  }

  @Override public void bindViewHolder(CountriesAdapterHolders.VIEWTYPE_COUNTRYViewHolder vh,
      int position) {
    vh.name.setText(countries.get(position).getName());
  }
}


