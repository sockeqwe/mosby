package com.hannesdorfmann.mosby.sample.dagger2.repos;

import com.hannesdorfmann.mosby.mvp.rx.lce.MvpLceRxPresenter;
import com.hannesdorfmann.mosby.sample.dagger2.model.GithubApi;
import com.hannesdorfmann.mosby.sample.dagger2.model.Repo;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * @author Hannes Dorfmann
 */
public class ReposPresenter extends MvpLceRxPresenter<ReposView, List<Repo>> {

  GithubApi githubapi;

  @Inject public ReposPresenter(GithubApi githubapi) {
    this.githubapi = githubapi;
  }

  public void loadRepos(boolean pullToRefresh) {
    Observable<List<Repo>> observable = githubapi.getRepos();

    subscribe(observable, pullToRefresh);
  }
}
