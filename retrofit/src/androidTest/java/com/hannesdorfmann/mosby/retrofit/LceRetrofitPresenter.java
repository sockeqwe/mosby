package com.hannesdorfmann.mosby.retrofit;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.retrofit.exception.UnexpectedStatusCodeException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A Presenter that can be used with {@link
 * MvpLceView} and Retrofit. It provides {@link LceCallback} that can be used as {@link Callback}
 * for retrofit requests.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class LceRetrofitPresenter<V extends MvpLceView<M>, M> extends MvpBasePresenter<V> {

  /**
   * An out of the box {@link Callback} implementation for Retrofit requests that are associated
   * with this presenter. It automatically calls {@link MvpLceView#setData(Object)}, {@link
   * MvpLceView#showContent()} or {@link MvpLceView#showError(Throwable, boolean)} if the view is
   * still attached to the presenter
   *
   * @author Hannes Dorfmann
   * @since 1.0.0
   */
  protected class LceCallback implements Callback<M> {

    private final boolean pullToRefresh;

    public LceCallback(boolean pullToRefresh) {
      this.pullToRefresh = pullToRefresh;
    }

    @Override public void success(M m, Response response) {
      if (isViewAttached()) {
        getView().setData(m);
        getView().showContent();
      }
    }

    @Override public void failure(RetrofitError error) {
      if (isViewAttached()) {
        Throwable t;
        if (error.getKind() == RetrofitError.Kind.HTTP && error.getResponse() != null) {
          t = new UnexpectedStatusCodeException(error.getResponse().getStatus());
        } else {
          t = error.getCause();
        }

        getView().showError(t, pullToRefresh);
      }
    }
  }
}
