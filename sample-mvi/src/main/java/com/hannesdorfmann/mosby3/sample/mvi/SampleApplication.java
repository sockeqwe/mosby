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

package com.hannesdorfmann.mosby3.sample.mvi;

import android.app.Application;
import android.content.Context;
import com.hannesdorfmann.mosby3.sample.mvi.dependencyinjection.DependencyInjection;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

/**
 * A custom Application class mainly used to provide dependency injection
 *
 * @author Hannes Dorfmann
 */
public class SampleApplication extends Application {

  protected DependencyInjection dependencyInjection = new DependencyInjection();

  {
    Timber.plant(new Timber.DebugTree());
  }

  public static DependencyInjection getDependencyInjection(Context context) {
    return ((SampleApplication) context.getApplicationContext()).dependencyInjection;
  }


  public static RefWatcher getRefWatcher(Context context) {
    SampleApplication application = (SampleApplication) context.getApplicationContext();
    return application.refWatcher;
  }

  private RefWatcher refWatcher;

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(this);
    Timber.d("Starting Application");
  }
}
