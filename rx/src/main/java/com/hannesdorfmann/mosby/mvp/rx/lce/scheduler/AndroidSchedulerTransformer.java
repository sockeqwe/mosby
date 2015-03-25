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

package com.hannesdorfmann.mosby.mvp.rx.lce.scheduler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The default {@link SchedulerTransformer} that subscrubes on {@link Schedulers#newThread()} and
 * observes on Android's main Thread.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class AndroidSchedulerTransformer<T> implements SchedulerTransformer<T> {

  @Override public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
  }
}
