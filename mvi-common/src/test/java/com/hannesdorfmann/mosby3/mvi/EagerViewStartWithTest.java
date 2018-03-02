package com.hannesdorfmann.mosby3.mvi;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class EagerViewStartWithTest {


    private static class EagerViewStartWith implements MvpView {

        List<String> renderedStates = new ArrayList<>();

        public Observable<String> intent1() {
            return Observable.just("Intent 1").startWith("Before Intent 1");
        }

        public Observable<String> intent2() {
            return Observable.just("Intent 2");
        }

        public void render(String state) {
            renderedStates.add(state);
        }
    }

    private static class EagerPresenter extends MviBasePresenter<EagerViewStartWith, String> {
        @Override
        protected void bindIntents() {
            Observable<String> intent1 = intent(new ViewIntentBinder<EagerViewStartWith, String>() {
                @NonNull
                @Override
                public Observable<String> bind(@NonNull EagerViewStartWith view) {
                    return view.intent1();
                }
            });

            Observable<String> intent2 = intent(new ViewIntentBinder<EagerViewStartWith, String>() {
                @NonNull
                @Override
                public Observable<String> bind(@NonNull EagerViewStartWith view) {
                    return view.intent2();
                }
            });

            Observable<String> res1 = intent1.flatMap(new Function<String, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(@io.reactivex.annotations.NonNull String s)
                        throws Exception {
                    return Observable.just(s + " - Result 1");
                }
            });

            Observable<String> res2 = intent2.flatMap(new Function<String, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(@io.reactivex.annotations.NonNull String s)
                        throws Exception {
                    return Observable.just(s + " - Result 2");
                }
            });

            Observable<String> merged = Observable.merge(res1, res2);

            subscribeViewState(merged, new ViewStateConsumer<EagerViewStartWith, String>() {
                @Override
                public void accept(@NonNull EagerViewStartWith view, @NonNull String viewState) {
                    view.render(viewState);
                }
            });
        }
    }


    @Test
    public void viewWithStartWithIntentWorksProperly() {
        EagerViewStartWith view = new EagerViewStartWith();
        EagerPresenter presenter = new EagerPresenter();
        presenter.attachView(view);

        Assert.assertEquals(Arrays.asList("Before Intent 1 - Result 1", "Intent 1 - Result 1", "Intent 2 - Result 2"), view.renderedStates);

    }

}
