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

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.sample.mvi.dependencyinjection.DependencyInjection;
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

  @Override public void onCreate() {
    super.onCreate();
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.d("Activity created %s", savedInstanceState);
      }

      @Override public void onActivityStarted(Activity activity) {

      }

      @Override public void onActivityResumed(Activity activity) {

      }

      @Override public void onActivityPaused(Activity activity) {

      }

      @Override public void onActivityStopped(Activity activity) {

      }

      @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.d("Activity save state");
      }

      @Override public void onActivityDestroyed(Activity activity) {
        Timber.d("Activity destroyed‚");
      }
    });
  }
}
