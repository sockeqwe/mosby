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

package com.hannesdorfmann.mosby3.utils.fragment.integrationtest.backstack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BackstackAccessor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hannesdorfmann.mosby3.utils.fragment.integrationtest.R;

/**
 * @author Hannes Dorfmann
 */

public class ReplaceTransactionFragment extends Fragment {

  public static int onBackStackCount = 0;
  public static int notOnBackStackCount = 0;
  private static final String KEY_ON_BACKSTACK = "onBackStack";

  private boolean onBackStack = false;

  public static ReplaceTransactionFragment newInstance(boolean onBackStack) {
    ReplaceTransactionFragment f = new ReplaceTransactionFragment();
    Bundle args = new Bundle();
    args.putBoolean(KEY_ON_BACKSTACK, onBackStack);
    f.setArguments(args);
    return f;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    onBackStack = getArguments().getBoolean(KEY_ON_BACKSTACK);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_child, container, false);
    TextView tv = (TextView) view.findViewById(R.id.text);
    tv.setText(toString());

    return view;
  }

  @Override public void onStop() {
    super.onStop();
    boolean onBackStack = this.onBackStack;
    boolean foundOnBackStack = BackstackAccessor.isFragmentOnBackStack(this);
    if (onBackStack != foundOnBackStack) {

      boolean refound = BackstackAccessor.isFragmentOnBackStack(this);

      throw new IllegalStateException(onBackStack
          + " "
          + foundOnBackStack
          + " --- Fragment should be onBackstack: "
          + onBackStack
          + " but was found on backstack: "
          + foundOnBackStack
          + "  "
          + toString()
          + " "
          + refound);
    }
    if (foundOnBackStack) {
      onBackStackCount++;
    } else {
      notOnBackStackCount++;
    }
  }
}
