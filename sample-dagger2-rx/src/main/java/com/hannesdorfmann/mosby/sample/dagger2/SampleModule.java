package com.hannesdorfmann.mosby.sample.dagger2;

import android.content.Context;
import com.hannesdorfmann.mosby.sample.dagger2.model.ErrorMessageDeterminer;
import com.hannesdorfmann.mosby.sample.dagger2.model.GithubApi;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Hannes Dorfmann
 */
@Module(
)
public class SampleModule {

  private Context context;

  public SampleModule(Context context) {
    this.context = context;
  }

  @Provides public Context provideContext() {
    return context;
  }

  @Provides @Singleton Picasso providesPicasso() {
    return Picasso.with(context);
  }


  @Provides @Singleton public GithubApi providesGithubApi() {

    OkHttpClient client = new OkHttpClient();
    client.setCache(new Cache(context.getCacheDir(), 10 * 1024 * 1024));

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setClient(new OkClient(client))
        .setEndpoint("https://api.github.com")
        .build();

    return restAdapter.create(GithubApi.class);
  }

  @Provides @Singleton
  public ErrorMessageDeterminer providesErrorMessageDeterminer(){
    return new ErrorMessageDeterminer();
  }

}
