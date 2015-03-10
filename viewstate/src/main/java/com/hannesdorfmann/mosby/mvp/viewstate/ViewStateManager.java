/*
 * Copyright (c) 2015 Hannes Dorfmann.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.mvp.viewstate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * It's just a little helper / utils class that avoids to many copy & paste code clones. It allows
 * to use this class to handle {@link ViewState} by using {@link ViewStateable}
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class ViewStateManager {

  /**
   * Like the name already suggests. Creates a new viewstate or tries to restore the old one (must
   * be subclass of {@link ParcelableViewState}) by reading the bundle
   *
   * @return true, if the viewstate has been restored (in other words {@link
   * ViewState#apply(MvpView)} has been invoked). Otherwise returns false, but calls
   * {@link
   * ViewStateable#onEmptyViewState()} before returning false
   */
  public static <V extends MvpView> boolean createOrRestore(@NonNull ViewStateable viewStateable,
      @NonNull V view, Bundle savedInstanceState) {

    // ViewState already exists (Fragment retainsInstanceState == true)
    if (viewStateable.getViewState() != null) {
      viewStateable.getViewState().apply(view);
      return true;
    }

    viewStateable.setViewState(viewStateable.createViewState());
    if (viewStateable.getViewState() == null) {
      throw new NullPointerException(
          "ViewState is null! Do you return null in createViewState() method?");
    }

    // Try to restore data from bundle (savedInstanceState)
    if (savedInstanceState != null && viewStateable.getViewState() instanceof ParcelableViewState) {

      boolean restoredFromBundle =
          ((ParcelableViewState) viewStateable.getViewState()).restoreInstanceState(
              savedInstanceState);

      if (restoredFromBundle) {
        viewStateable.getViewState().apply(view);
        return true;
      }
    }

    // ViewState not restored
    viewStateable.onEmptyViewState();
    return false;
  }

  /**
   * Saves {@link ParcelableViewState} in a bundle. <b>Should be calld from activities or fragments
   * onSaveInstanceState(Bundle) method</b>
   */
  public static void saveInstanceState(ViewStateable viewStateable, Bundle outState) {

    if (viewStateable.getViewState() == null) {
      throw new NullPointerException("ViewState is null! That's not allowed");
    }

    // Save the viewstate
    if (viewStateable.getViewState() instanceof ParcelableViewState) {
      ((ParcelableViewState) viewStateable.getViewState()).saveInstanceState(outState);
    }
  }
}
