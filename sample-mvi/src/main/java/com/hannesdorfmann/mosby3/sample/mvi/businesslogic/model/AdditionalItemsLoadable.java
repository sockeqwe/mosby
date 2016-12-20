/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This is a indicator that also some more items are available that could be loaded
 *
 * @author Hannes Dorfmann
 */
public class AdditionalItemsLoadable implements FeedItem {
  private final int moreItemsAvailableCount;
  private final String groupName;
  private final boolean loading;
  private final Throwable loadingError;

  public AdditionalItemsLoadable(int moreItemsAvailableCount, @NonNull String groupName,
      boolean loading, @Nullable Throwable loadingError) {
    this.moreItemsAvailableCount = moreItemsAvailableCount;
    this.groupName = groupName;
    this.loading = loading;
    this.loadingError = loadingError;
  }

  public int getMoreItemsCount() {
    return moreItemsAvailableCount;
  }

  @NonNull public String getCategoryName() {
    return groupName;
  }

  public int getMoreItemsAvailableCount() {
    return moreItemsAvailableCount;
  }

  public boolean isLoading() {
    return loading;
  }

  public Throwable getLoadingError() {
    return loadingError;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AdditionalItemsLoadable that = (AdditionalItemsLoadable) o;

    if (moreItemsAvailableCount != that.moreItemsAvailableCount) return false;
    if (loading != that.loading) return false;
    if (!groupName.equals(that.groupName)) return false;
    return loadingError != null ? loadingError.equals(that.loadingError)
        : that.loadingError == null;
  }

  @Override public int hashCode() {
    int result = moreItemsAvailableCount;
    result = 31 * result + groupName.hashCode();
    result = 31 * result + (loading ? 1 : 0);
    result = 31 * result + (loadingError != null ? loadingError.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "AdditionalItemsLoadable{" +
        "moreItemsAvailableCount=" + moreItemsAvailableCount +
        ", groupName='" + groupName + '\'' +
        ", loading=" + loading +
        ", loadingError=" + loadingError +
        '}';
  }
}
