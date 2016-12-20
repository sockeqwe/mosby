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

/**
 * This is a indicator that also some more items are available that could be loaded
 *
 * @author Hannes Dorfmann
 */
public class AdditionalItemsLoadable implements FeedItem {
  private final int moreItemsAvailableCount;
  private final String groupName;

  public AdditionalItemsLoadable(int moreItemsAvailableCount, @NonNull String groupName) {
    this.moreItemsAvailableCount = moreItemsAvailableCount;
    this.groupName = groupName;
  }

  public int getMoreItemsCount() {
    return moreItemsAvailableCount;
  }

  @NonNull public String getGroupName() {
    return groupName;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AdditionalItemsLoadable that = (AdditionalItemsLoadable) o;

    if (moreItemsAvailableCount != that.moreItemsAvailableCount) return false;
    return groupName.equals(that.groupName);
  }

  @Override public int hashCode() {
    int result = moreItemsAvailableCount;
    result = 31 * result + groupName.hashCode();
    return result;
  }
}
