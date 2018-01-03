/*
 * Copyright 2017 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.sample.mvi.view.home;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;

/**
 * This class is responsible to drive the HomeView.
 * Internally this creates a {@link HomeView} and attaches it to the {@link HomePresenter}
 * and offers public API to fire view intents and to check for expected view.render() events.
 *
 * <p>
 * <b>Create a new instance for every unit test</b>
 * </p>
 *
 * @author Hannes Dorfmann
 */
public class HomeViewRobot {

  private final HomePresenter presenter;
  private final PublishSubject<Boolean> loadFirstPageSubject = PublishSubject.create();
  private final PublishSubject<Boolean> loadNextPageSubject = PublishSubject.create();
  private final PublishSubject<Boolean> pullToRefreshSubject = PublishSubject.create();
  private final PublishSubject<String> loadAllProductsFromCategorySubject = PublishSubject.create();
  private final List<HomeViewState> renderEvents = new CopyOnWriteArrayList<>();
  private final ReplaySubject<HomeViewState> renderEventSubject = ReplaySubject.create();

  private HomeView view = new HomeView() {
    @Override public Observable<Boolean> loadFirstPageIntent() {
      return loadFirstPageSubject;
    }

    @Override public Observable<Boolean> loadNextPageIntent() {
      return loadNextPageSubject;
    }

    @Override public Observable<Boolean> pullToRefreshIntent() {
      return pullToRefreshSubject;
    }

    @Override public Observable<String> loadAllProductsFromCategoryIntent() {
      return loadAllProductsFromCategorySubject;
    }

    @Override public void render(HomeViewState viewState) {
      renderEvents.add(viewState);
      renderEventSubject.onNext(viewState);
    }
  };

  public HomeViewRobot(HomePresenter presenter) {
    this.presenter = presenter;
    presenter.attachView(view);
  }

  public void fireLoadFirstPageIntent() {
    loadFirstPageSubject.onNext(true);
  }

  public void fireLoadNextPageIntent() {
    loadNextPageSubject.onNext(true);
  }

  public void firePullToRefreshIntent() {
    pullToRefreshSubject.onNext(true);
  }

  public void fireLoadAllProductsFromCategory(String category) {
    loadAllProductsFromCategorySubject.onNext(category);
  }

  /**
   * Blocking waits for view.render() calls and
   *
   * @param expectedHomeViewStates The expected  HomeViewStates that will be passed to
   * view.render()
   */
  public void assertViewStateRendered(HomeViewState... expectedHomeViewStates) {

    if (expectedHomeViewStates == null) {
      throw new NullPointerException("expectedHomeViewStates == null");
    }

    int eventsCount = expectedHomeViewStates.length;
    renderEventSubject.take(eventsCount)
        .timeout(10, TimeUnit.SECONDS)
        .blockingSubscribe();

    /*
    // Wait for few milli seconds to ensure that no more render events have occurred
    // before finishing the test and checking expectations (asserts)
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    */

    if (renderEventSubject.getValues().length > eventsCount) {
      Assert.fail("Expected to wait for "
          + eventsCount
          + ", but there were "
          + renderEventSubject.getValues().length
          + " Events in total, which is more than expected: "
          + arrayToString(renderEventSubject.getValues()));
    }

    Assert.assertEquals(Arrays.asList(expectedHomeViewStates), renderEvents);
  }

  /**
   * Simple helper function to print the content of an array as a string
   */
  private String arrayToString(Object[] array) {
    StringBuffer buffer = new StringBuffer();
    for (Object o : array) {
      buffer.append(o.toString());
      buffer.append("\n");
    }

    return buffer.toString();
  }
}
