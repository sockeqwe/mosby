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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */

public class EagerObservableFlowTest {

  class SomeView implements MvpView {

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

  public class EagerPresenter extends MviBasePresenter<SomeView, String> {
    @Override protected void bindIntents() {
      Observable<String> intent1 = intent(new ViewIntentBinder<SomeView, String>() {
        @NonNull @Override public Observable<String> bind(@NonNull SomeView view) {
          return view.intent1();
        }
      });

      Observable<String> intent2 = intent(new ViewIntentBinder<SomeView, String>() {
        @NonNull @Override public Observable<String> bind(@NonNull SomeView view) {
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

      subscribeViewState(merged, new ViewStateConsumer<SomeView, String>() {
        @Override public void accept(@NonNull SomeView view, @NonNull String viewState) {
          view.render(viewState);
        }
      });
    }
  }

  @Test public void connectEager() {

    SomeView view = new SomeView();
    EagerPresenter presenter = new EagerPresenter();

    presenter.attachView(view);

    Assert.assertEquals(Arrays.asList("Result 1", "Result 2"), view.renderedStates);
  }

}
