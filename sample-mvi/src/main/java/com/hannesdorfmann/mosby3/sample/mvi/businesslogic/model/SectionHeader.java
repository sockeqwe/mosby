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
 * A section header used to group elemens
 *
 * @author Hannes Dorfmann
 */
public class SectionHeader implements FeedItem {
  private final String name;

  public SectionHeader(@NonNull String name) {
    this.name = name;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SectionHeader that = (SectionHeader) o;

    return name.equals(that.name);
  }

  @Override public int hashCode() {
    return name.hashCode();
  }

  @Override public String toString() {
    return "SectionHeader{" +
        "name='" + name + '\'' +
        '}';
  }
}
