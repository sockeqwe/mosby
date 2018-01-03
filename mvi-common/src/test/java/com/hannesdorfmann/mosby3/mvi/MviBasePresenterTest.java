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

package com.hannesdorfmann.mosby3.mvi;

import android.support.annotation.NonNull;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.Object;

/**
 * @author Hannes Dorfmann
 */
public class MviBasePresenterTest {

  @Test public void bindIntentsAndUnbindIntentsOnlyOnce() {

    final AtomicInteger bindInvocations = new AtomicInteger(0);
    final AtomicInteger unbindInvocations = new AtomicInteger(0);

    MvpView view = new MvpView() {
    };

    MviBasePresenter<MvpView, Object> presenter = new MviBasePresenter<MvpView, Object>() {
      @Override protected void bindIntents() {
        bindInvocations.incrementAndGet();
      }

      @Override protected void unbindIntents() {
        super.unbindIntents();
        unbindInvocations.incrementAndGet();
      }
    };

    presenter.attachView(view);
    presenter.detachView();
    presenter.attachView(view);
    presenter.detachView();
    presenter.attachView(view);
    presenter.detachView();
    presenter.destroy();

    Assert.assertEquals(1, bindInvocations.get());
    Assert.assertEquals(1, unbindInvocations.get());
  }

  @Test public void keepUnderlayingSubscriptions() {
    final List<String> intentsData = new ArrayList<>();
    final PublishSubject<String> businessLogic = PublishSubject.create();
    KeepUndelyingSubscriptionsView view = new KeepUndelyingSubscriptionsView();
    MviBasePresenter<KeepUndelyingSubscriptionsView, String> presenter =
        new MviBasePresenter<KeepUndelyingSubscriptionsView, String>() {
          @Override protected void bindIntents() {

            intent(new ViewIntentBinder<KeepUndelyingSubscriptionsView, String>() {
              @NonNull @Override
              public Observable<String> bind(@NonNull KeepUndelyingSubscriptionsView view) {
                return view.anIntent;
              }
            }).subscribe(new Consumer<String>() {
              @Override public void accept(String s) throws Exception {
                intentsData.add(s);
              }
            });

            subscribeViewState(businessLogic,
                new ViewStateConsumer<KeepUndelyingSubscriptionsView, String>() {
                  @Override public void accept(@NonNull KeepUndelyingSubscriptionsView view,
                      @NonNull String viewState) {
                    view.render(viewState);
                  }
                });
          }
        };

    view.anIntent.onNext("Should never hit the presenter because View not attached");
    Assert.assertTrue(intentsData.isEmpty());

    presenter.attachView(view);
    view.anIntent.onNext("1 Intent");
    Assert.assertEquals(Arrays.asList("1 Intent"), intentsData);
    businessLogic.onNext("1 bl");
    Assert.assertEquals(Arrays.asList("1 bl"), view.renderedModels);
    businessLogic.onNext("2 bl");
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl"), view.renderedModels);
    view.anIntent.onNext("2 Intent");
    Assert.assertEquals(Arrays.asList("1 Intent", "2 Intent"), intentsData);

    // Detach View temporarily
    presenter.detachView();
    Assert.assertFalse(view.anIntent.hasObservers());
    businessLogic.onNext("3 bl");
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl"), view.renderedModels);
    businessLogic.onNext("4 bl");
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl"), view.renderedModels);
    view.anIntent.onNext("Doesn't hit presenter because view not attached to presenter");
    Assert.assertEquals(Arrays.asList("1 Intent", "2 Intent"), intentsData);

    // Reattach View
    presenter.attachView(view); // This will call view.render() method
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl", "4 bl"), view.renderedModels);
    view.anIntent.onNext("3 Intent");
    Assert.assertEquals(Arrays.asList("1 Intent", "2 Intent", "3 Intent"), intentsData);
    businessLogic.onNext("5 bl");
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl", "4 bl", "5 bl"), view.renderedModels);

    // Detach View permanently
    presenter.detachView();
    presenter.destroy();
    Assert.assertFalse(businessLogic.hasObservers()); // No more observers
    Assert.assertFalse(view.anIntent.hasObservers()); // No more observers
    view.anIntent.onNext("This will never be delivered to presenter");
    Assert.assertEquals(Arrays.asList("1 Intent", "2 Intent", "3 Intent"), intentsData);
    businessLogic.onNext("This will never reach the view");
    Assert.assertEquals(Arrays.asList("1 bl", "2 bl", "4 bl", "5 bl"), view.renderedModels);
  }

  @Test public void resetOnViewDetachedPermanently() {
    final AtomicInteger bindInvocations = new AtomicInteger(0);
    final AtomicInteger unbindInvocations = new AtomicInteger(0);

    MvpView view = new MvpView() {
    };

    MviBasePresenter<MvpView, Object> presenter = new MviBasePresenter<MvpView, Object>() {
      @Override protected void bindIntents() {
        bindInvocations.incrementAndGet();
      }

      @Override protected void unbindIntents() {
        super.unbindIntents();
        unbindInvocations.incrementAndGet();
      }
    };

    presenter.attachView(view);
    presenter.detachView();
    presenter.destroy();
    presenter.attachView(view);
    presenter.detachView();
    presenter.attachView(view);
    presenter.detachView();
    presenter.destroy();

    Assert.assertEquals(2, bindInvocations.get());
    Assert.assertEquals(2, unbindInvocations.get());
  }

  private static class KeepUndelyingSubscriptionsView implements MvpView {

    List<String> renderedModels = new ArrayList<>();

    PublishSubject<String> anIntent = PublishSubject.create();

    public void render(String model) {
      renderedModels.add(model);
    }
  }
}
