package com.hannesdorfmann.mosby.retrofit;

import com.hannesdorfmann.mosby.retrofit.mock.TestApi;

/**
 * @author Hannes Dorfmann
 */
public class TestPresenter extends LceRetrofitPresenter<TestView, Object> {

  TestApi api ;

  public TestPresenter(TestApi api) {
    this.api = api;
  }

  public void loadData(boolean pullToRefresh){
    api.loadData(new LceCallback(pullToRefresh));
  }
}
