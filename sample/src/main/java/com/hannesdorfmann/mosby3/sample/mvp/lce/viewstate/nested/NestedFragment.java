/*
 *  Copyright 2015. Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.nested;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hannesdorfmann.mosby3.sample.R;
import com.hannesdorfmann.mosby3.sample.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvp.lce.viewstate.NotRetainingCountriesFragment;

/**
 * @author Hannes Dorfmann
 */
public class NestedFragment extends Fragment {

  private final String NESTED_TAG = "nestedFrag";


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_nested_container, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Fragment nestedFragment = getChildFragmentManager().findFragmentByTag(NESTED_TAG);
    if (nestedFragment == null) {
      getChildFragmentManager().beginTransaction()
          .replace(R.id.nestedFragmentContainer, new NotRetainingCountriesFragment(), NESTED_TAG)
          .commit();
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    SampleApplication.getRefWatcher(getActivity()).watch(this);
  }
}
