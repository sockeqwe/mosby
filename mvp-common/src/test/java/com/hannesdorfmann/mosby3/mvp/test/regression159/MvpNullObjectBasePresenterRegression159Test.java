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

package com.hannesdorfmann.mosby3.mvp.test.regression159;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test for this issue:
 * https://github.com/sockeqwe/mosby/issues/159
 *
 * @author Hannes Dorfmann
 */
public class MvpNullObjectBasePresenterRegression159Test {

  @Test public void mvpNullObjectPresenterCreatesNullObjectProperly() {

    RecentArticlesPresenter presenter = new RecentArticlesPresenter();
    final AtomicInteger invocations = new AtomicInteger(0);

    RecentArticles.View spyView = new RecentArticles.View() {

      @Override public void updateData(List<Article> data) {
        invocations.incrementAndGet();
      }
    };

    presenter.attachView(spyView);
    presenter.invokeAMethodOnView();
    Assert.assertEquals(1, invocations.get());
    presenter.detachView(true);

    // Invoke a method while view is detached. This should invoke the method on the null view
    presenter.invokeAMethodOnView();
    Assert.assertEquals(1, invocations.get()); // Not incremented because invoked on null view

    presenter.attachView(spyView);
    presenter.invokeAMethodOnView();
    Assert.assertEquals(2, invocations.get());
    presenter.detachView(false);

    // Invoke a method while view is detached. This should invoke the method on the null view
    presenter.invokeAMethodOnView();
    Assert.assertEquals(2, invocations.get()); // Not incremented because invoked on null view
  }
}
