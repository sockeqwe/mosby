package com.hannesdorfmann.mosby.sample.dagger2.repos;

import com.hannesdorfmann.mosby.sample.dagger2.model.GithubApi;
import com.hannesdorfmann.mosby.sample.dagger2.model.Repo;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
public class ReposPresenter extends MvpLceRxPresenter<ReposView, List<Repo>> {

  GithubApi githubapi;

  @Inject public ReposPresenter(GithubApi githubapi) {
    this.githubapi = githubapi;
  }

  public void loadRepos(boolean pullToRefresh) {

    Observable<List<Repo>> observable =
        githubapi.getRepos().flatMap(new Func1<List<Repo>, Observable<List<Repo>>>() {
          @Override public Observable<List<Repo>> call(List<Repo> repos) {
            Collections.shuffle(repos);
            return Observable.just(repos);
          }
        });

    subscribe(observable, pullToRefresh);
  }


}
