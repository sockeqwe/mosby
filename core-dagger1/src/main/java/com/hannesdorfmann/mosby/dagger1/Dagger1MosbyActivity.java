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

package com.hannesdorfmann.mosby.dagger1;

import android.app.Application;
import com.hannesdorfmann.mosby.MosbyActivity;
import dagger.ObjectGraph;

/**
 * {@link MosbyActivity} with Dagger 1 support, because this activity impelements {@link Injector}.
 *
 * <p>
 * Does not automatically inject itself dependencies. To do so please override {@link
 * #injectDependencies()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class Dagger1MosbyActivity extends MosbyActivity implements Injector {

  /**
   * Returns daggers object graph. As default {@link Dagger1MosbyActivity} assumes that your {@link
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
}
