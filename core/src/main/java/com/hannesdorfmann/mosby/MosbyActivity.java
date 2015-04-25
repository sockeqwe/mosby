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

package com.hannesdorfmann.mosby;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * A simple activity that uses Butterknife and IcePick.
 *
 * <p>
 * If you want to use dependency injection libraries like dagger you can override {@link
 * #injectDependencies()} and implement dependency injection right there
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class MosbyActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependencies();
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override public void onContentChanged() {
    super.onContentChanged();
    ButterKnife.inject(this);
  }

  /**
   * This method will be called from {@link #onCreate(Bundle)} and this is the right place to
   * inject
   * dependencies (i.e. by using dagger)
   */
  protected void injectDependencies() {

  }
}
