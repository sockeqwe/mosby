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

package com.hannesdorfmann.mosby.dagger1.viewstate.lce;

import android.view.View;
import com.hannesdorfmann.mosby.dagger1.Injector;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import dagger.ObjectGraph;

/**
 * @author Hannes Dorfmann
 */
public abstract class Dagger1MvpLceViewStateFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpLceViewStateFragment<CV, M, V, P> implements Injector {

  @Override public ObjectGraph getObjectGraph() {

    if (getActivity() == null) {
      throw new NullPointerException("Activity is null");
    }

    if (!(getActivity() instanceof Injector)) {
      throw new IllegalArgumentException(getActivity()
          + " must implement "
          + Injector.class.getCanonicalName()
          + ","
          + " because as default it uses Activity.getObjectGraph(). "
          + "However, you can override this behaviour by overriding this method.");
    }

    return ((Injector) getActivity()).getObjectGraph();
  }

}
