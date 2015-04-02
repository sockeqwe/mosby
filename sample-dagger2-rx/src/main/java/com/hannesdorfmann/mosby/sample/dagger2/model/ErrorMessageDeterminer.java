package com.hannesdorfmann.mosby.sample.dagger2.model;

import retrofit.RetrofitError;

/**
 * @author Hannes Dorfmann
 */
public class ErrorMessageDeterminer {

  public String getErrorMessage(Throwable e, boolean pullToRefresh) {
    if (e instanceof RetrofitError && ((RetrofitError) e).getKind() == RetrofitError.Kind.NETWORK) {
      return "Network Error: Are you connected to the internet?";
    }

    return "An unknown error has occurred";
  }
}
