package com.hannesdorfmann.mosby.sample.mail.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * This {@link BaseAdapter} encapsulate the getView() call into
 * {@link #newView(int, ViewGroup)} and {@link #bindView(int, int, View)}
 *
 * @author Hannes Dorfmann
 */
public abstract class SimpleAdapter<D extends List> extends BaseAdapter {

  /**
   * The inflater for
   */
  protected LayoutInflater inflater;
  protected Context context;
  protected D items;

  public SimpleAdapter(Context context) {
    this.context = context;
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public void setItems(D items) {
    this.items = items;
  }

  public D getItems() {
    return items;
  }

  public void setInflater(LayoutInflater inflater) {
    this.inflater = inflater;
  }

  @Override
  public int getCount() {
    return items == null ? 0 : items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);
    if (convertView == null) {
      convertView = newView(type, parent);
    }
    bindView(position, type, convertView);
    return convertView;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  /** Create a new instance of a view for the specified {@code type}. */
  public abstract View newView(int type, ViewGroup parent);

  /** Bind the data for the specified {@code position} to the {@code view}. */
  public abstract void bindView(int position, int type, View view);
}