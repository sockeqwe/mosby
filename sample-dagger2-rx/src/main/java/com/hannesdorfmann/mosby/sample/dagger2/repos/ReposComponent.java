package com.hannesdorfmann.mosby.sample.dagger2.repos;

import com.hannesdorfmann.mosby.sample.dagger2.SampleModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Singleton @Component(
    modules = SampleModule.class)
public interface ReposComponent {

  public void inject(ReposFragment fragment);

  public ReposPresenter presenter();

  public ReposAdapter adapter();
}
