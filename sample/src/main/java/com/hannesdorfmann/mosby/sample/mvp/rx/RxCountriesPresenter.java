package com.hannesdorfmann.mosby.sample.mvp.rx;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import com.hannesdorfmann.mosby.sample.mvp.model.CountryApi;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
public class RxCountriesPresenter extends MvpLceRxPresenter<CountriesView, List<Country>>
    implements CountriesPresenter {

  private int failingCounter = 0;

  @Override public void loadCountries(boolean pullToRefresh) {
    subscribe(pullToRefresh);
  }

  @Override protected Observable<List<Country>> getObservable() {
    return Observable.create(new Observable.OnSubscribe<List<Country>>() {
      @Override public void call(Subscriber<? super List<Country>> subscriber) {
        List<Country> countries = CountryApi.getCountries();
        subscriber.setProducer(new Producer() {
          @Override public void request(long n) {

          }
        });
      }
    }.flatMap(new Func1<List<Country>, Observable<List<Country>>>() {
      @Override public Observable<List<Country>> call(List<Country> countries) {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return null;
        }

        failingCounter++;
        if (failingCounter % 2 == 1) {
          throw new RuntimeException("Oops something went wrong");
        }

        Collections.shuffle(countries);
        return Observable.just(countries);
      }
    });
  }
}
