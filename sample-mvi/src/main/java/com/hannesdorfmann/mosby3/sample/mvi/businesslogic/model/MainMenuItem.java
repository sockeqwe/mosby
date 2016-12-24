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

/**
 * Represents a main menu item that can be selected
 *
 * @author Hannes Dorfmann
 */
public class MainMenuItem {
  /**
   * Preserved "category" name for the menu item that triggers to the "home" screen
   */
  public static final String HOME = "Home";

  private final String name;
  private final boolean selected;

  public MainMenuItem(String name, boolean selected) {
    this.name = name;
    this.selected = selected;
  }

  public String getName() {
    return name;
  }

  public boolean isSelected() {
    return selected;
  }

  @Override public String toString() {
    return "MainMenuItem{" +
        "name='" + name + '\'' +
        ", selected=" + selected +
        '}';
  }
}
