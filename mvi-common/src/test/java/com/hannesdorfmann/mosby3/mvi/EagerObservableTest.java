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
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test that test if observables (like intents) are submitting directly in on subscribe,
 * that all events will be dispatched properly to view.render() and no one has been swallowed
 * because the observable stream hasn't been fully established yet.
 * @author Hannes Dorfmann
 */
public class EagerObservableTest {

  private static class EagerView implements MvpView {

    List<String> renderedStates = new ArrayList<>();

    public Observable<String> intent1() {
      return Observable.just("Intent 1");
    }

    public Observable<String> intent2() {
      return Observable.just("Intent 2");
    }

    public void render(String state) {
      renderedStates.add(state);
    }
  }

  private static class EagerPresenter extends MviBasePresenter<EagerView, String> {
    @Override protected void bindIntents() {
      Observable<String> intent1 = intent(new ViewIntentBinder<EagerView, String>() {
        @NonNull @Override public Observable<String> bind(@NonNull EagerView view) {
          return view.intent1();
        }
      });

      Observable<String> intent2 = intent(new ViewIntentBinder<EagerView, String>() {
        @NonNull @Override public Observable<String> bind(@NonNull EagerView view) {
          return view.intent2();
        }
      });

      Observable<String> res1 = intent1.flatMap(new Function<String, ObservableSource<String>>() {
        @Override public ObservableSource<String> apply(@io.reactivex.annotations.NonNull String s)
            throws Exception {
          return Observable.just("Result 1");
        }
      });

      Observable<String> res2 = intent2.flatMap(new Function<String, ObservableSource<String>>() {
        @Override public ObservableSource<String> apply(@io.reactivex.annotations.NonNull String s)
            throws Exception {
          return Observable.just("Result 2");
        }
      });

      Observable<String> merged = Observable.merge(res1, res2);

      subscribeViewState(merged, new ViewStateConsumer<EagerView, String>() {
        @Override public void accept(@NonNull EagerView view, @NonNull String viewState) {
          view.render(viewState);
        }
      });
    }
  }

  @Test public void connectEager() {

    EagerView view = new EagerView();
    EagerPresenter presenter = new EagerPresenter();

    presenter.attachView(view);

    Assert.assertEquals(Arrays.asList("Result 1", "Result 2"), view.renderedStates);
  }

}
