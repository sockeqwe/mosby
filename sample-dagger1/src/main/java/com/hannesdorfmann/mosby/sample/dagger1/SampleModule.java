/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.sample.dagger1;

import android.content.Context;
import com.hannesdorfmann.mosby.sample.dagger1.model.GithubApi;
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
@Module public class SampleModule {

  private RestAdapter restAdapter;
  private Context applicationContext;

  public SampleModule(Context context) {

    this.applicationContext = context;

    OkHttpClient client = new OkHttpClient();
    client.setCache(new Cache(context.getCacheDir(), 10 * 1024 * 1024));

    restAdapter = new RestAdapter.Builder().setClient(new OkClient(client))
        .setEndpoint("https://api.github.com")
        .build();
  }

  @Provides @Singleton public GithubApi providesGithubApi() {
    return restAdapter.create(GithubApi.class);
  }

  @Provides @Singleton Picasso providesPicasso(){
    return Picasso.with(applicationContext);
  }

}
