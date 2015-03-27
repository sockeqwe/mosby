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

package com.hannesdorfmann.mosby.dagger1.mvp.lce;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import com.hannesdorfmann.mosby.dagger1.Injector;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import dagger.ObjectGraph;

/**
 * {@link MvpLceActivity} with dagger 1 support
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class Dagger1MvpLceActivity<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpLceActivity<CV, M, V, P> implements Injector {

  /**
   * Returns daggers object graph. As defaultit assumes that your {@link
   * Application} implements {@link Injector} and that one will be accessed. If you want to do your
   * own thing override this method. However, injecting should not be done in this method but in
   * {@link #injectDependencies()}
   *
   * @see #injectDependencies()
   */
  @Override public ObjectGraph getObjectGraph() {

    if (getApplication() == null) {
      throw new NullPointerException("Application is null");
    }

    if (!(getApplication() instanceof Injector)) {
      throw new IllegalArgumentException("You are using "
          + this.getClass()
          + " for injecting Dagger."
          + " This requires that your Application implements "
          + Injector.class.getCanonicalName()
          + ". Alternatively you can override getObjectGraph() in your Activity to fit your needs");
    }

    Injector appInjector = (Injector) getApplication();
    return appInjector.getObjectGraph();
  }

  /**
   * Called from {@link #onCreate(Bundle)}. As default it calls {@link #getObjectGraph()} and
   * injects to this activity
   */
  @Override protected void injectDependencies() {
    getObjectGraph().inject(this);
  }
}
