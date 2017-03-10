/*
 * Copyright 2017 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvi.view.home;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.AdditionalItemsLoadable;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.FeedItem;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.SectionHeader;
import com.hannesdorfmann.mosby3.sample.mvi.dependencyinjection.DependencyInjection;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A simple unit test demonstrating how to write unit tests for MVI Presenters
 *
 * @author Hannes Dorfmann
 */
public class HomePresenterTest {

  // Json serializer for mock server
  private Moshi moshi = new Moshi.Builder().build();
  private Type type = Types.newParameterizedType(List.class, Product.class);
  private JsonAdapter<List<Product>> adapter = moshi.adapter(type);

  private MockWebServer mockWebServer;

  @BeforeClass public static void init() throws Exception {
    // Tell RxAndroid to not use android main ui thread scheduler
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(
        new Function<Callable<Scheduler>, Scheduler>() {
          @Override public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable)
              throws Exception {
            return Schedulers.trampoline();
          }
        });
  }

  @Before public void beforeEachTest() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    // Set the apps url to the local mock server
    DependencyInjection.BASE_URL = mockWebServer.url("").toString();
  }

  @After public void afterEachTest() throws Exception {
    mockWebServer.shutdown();
  }

  @AfterClass public static void tearDown() throws Exception {
    RxAndroidPlugins.reset();
  }

  @Test public void loadingFirstPage() {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProducts =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProducts)));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire an intent
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //
    List<FeedItem> expectedData = Arrays.asList(
        new SectionHeader("category1"),
        mockProducts.get(0),
        mockProducts.get(1),
        mockProducts.get(2),
        new AdditionalItemsLoadable(2, "category1", false, null)
    );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedData).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);
  }

  @Test public void loadingFirstFailsWithNoConnectionError() throws IOException {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    mockWebServer.shutdown(); // Simulate no internet connection to the server

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter = new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire an intent
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show error indicator
    //

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState errorFirstPage = new HomeViewState.Builder().firstPageError(new ConnectException()).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, errorFirstPage);
  }

  @Test public void loadingFirstPageAndNextPage() {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProductsFirstPage =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    List<Product> mockProductsNextPage =
        Arrays.asList(new Product(6, "image", "name", "category2", "description", 21.9),
            new Product(7, "image", "name", "category2", "description", 21.9),
            new Product(8, "image", "name", "category2", "description", 21.9),
            new Product(9, "image", "name", "category2", "description", 21.9));

    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsFirstPage)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsNextPage)));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter = new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire intents
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //

    List<FeedItem> expectedDataAfterFristPage = Arrays.asList(
        new SectionHeader("category1"),
        mockProductsFirstPage.get(0),
        mockProductsFirstPage.get(1),
        mockProductsFirstPage.get(2),
        new AdditionalItemsLoadable(2, "category1", false, null)
    );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedDataAfterFristPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Fire second intent
    //
    robot.fireLoadNextPageIntent();

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page content (plus original first page content)
    //
    List<FeedItem> expectedDataAfterNextPage = Arrays.asList(
        new SectionHeader("category1"),
        mockProductsFirstPage.get(0),
        mockProductsFirstPage.get(1),
        mockProductsFirstPage.get(2),
        new AdditionalItemsLoadable(2, "category1", false, null),
        new SectionHeader("category2"),
        mockProductsNextPage.get(0),
        mockProductsNextPage.get(1),
        mockProductsNextPage.get(2),
        new AdditionalItemsLoadable(1, "category2", false, null)
    );

    HomeViewState nextPageLoading =
        new HomeViewState.Builder().data(expectedDataAfterFristPage).nextPageLoading(true).build();
    HomeViewState nextPage = new HomeViewState.Builder().data(expectedDataAfterNextPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage);
  }

  @Test public void loadingFirstPageAndFailLoadingNextPage() throws Exception {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProductsFirstPage =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsFirstPage)));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire intents
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //

    List<FeedItem> expectedDataAfterFristPage =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null)
        );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedDataAfterFristPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Fire second intent
    //
    mockWebServer.shutdown(); // causes loading next page error
    robot.fireLoadNextPageIntent();

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page error (plus original first page content)
    //

    HomeViewState nextPageLoading =
        new HomeViewState.Builder().data(expectedDataAfterFristPage).nextPageLoading(true).build();
    HomeViewState nextPage = new HomeViewState.Builder().data(expectedDataAfterFristPage)
        .nextPageError(new ConnectException())
        .build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage);
  }

  @Test public void loadingFirstPageAndNextPageAndPullToRefresh() {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProductsFirstPage =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    List<Product> mockProductsNextPage =
        Arrays.asList(new Product(6, "image", "name", "category2", "description", 21.9),
            new Product(7, "image", "name", "category2", "description", 21.9),
            new Product(8, "image", "name", "category2", "description", 21.9),
            new Product(9, "image", "name", "category2", "description", 21.9));

    List<Product> mockProductsPullToRefresh =
        Arrays.asList(new Product(10, "image", "name", "category3", "description", 21.9),
            new Product(11, "image", "name", "category3", "description", 21.9),
            new Product(12, "image", "name", "category3", "description", 21.9));

    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsFirstPage)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsNextPage)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsPullToRefresh)));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire intents
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //

    List<FeedItem> expectedDataAfterFristPage =
        Arrays.asList(new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null)
        );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedDataAfterFristPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Fire next page intent
    //
    robot.fireLoadNextPageIntent();

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page content (plus original first page content)
    //
    List<FeedItem> expectedDataAfterNextPage =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null),
            new SectionHeader("category2"),
            mockProductsNextPage.get(0),
            mockProductsNextPage.get(1),
            mockProductsNextPage.get(2),
            new AdditionalItemsLoadable(1, "category2", false, null)
        );

    HomeViewState nextPageLoading =
        new HomeViewState.Builder().data(expectedDataAfterFristPage).nextPageLoading(true).build();
    HomeViewState nextPage = new HomeViewState.Builder().data(expectedDataAfterNextPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage);

    //
    // fire pull to refresh intent
    //
    robot.firePullToRefreshIntent();

    //
    // we expect that 6 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page content (plus original first page content)
    // 5. show loading - pull to refresh indicator
    // 6. show pull to refresh content (plus original first page + next page content)
    //
    List<FeedItem> expectedDataAfterPullToRefresh =
        Arrays.asList(
            new SectionHeader("category3"),
            mockProductsPullToRefresh.get(0),
            mockProductsPullToRefresh.get(1),
            mockProductsPullToRefresh.get(2),
            // No additional items loadable for category3
            new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null),
            new SectionHeader("category2"),
            mockProductsNextPage.get(0),
            mockProductsNextPage.get(1),
            mockProductsNextPage.get(2),
            new AdditionalItemsLoadable(1, "category2", false, null)
        );

    HomeViewState pullToRefreshLoading = new HomeViewState.Builder().data(expectedDataAfterNextPage)
        .pullToRefreshLoading(true)
        .build();
    HomeViewState pullToRefreshPage =
        new HomeViewState.Builder().data(expectedDataAfterPullToRefresh).build();

    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage,
        pullToRefreshLoading, pullToRefreshPage);
  }

  @Test public void loadingFirstPageAndNextPageAndFailPullToRefresh() throws IOException {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProductsFirstPage =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    List<Product> mockProductsNextPage =
        Arrays.asList(new Product(6, "image", "name", "category2", "description", 21.9),
            new Product(7, "image", "name", "category2", "description", 21.9),
            new Product(8, "image", "name", "category2", "description", 21.9),
            new Product(9, "image", "name", "category2", "description", 21.9));

    List<Product> mockProductsPullToRefresh =
        Arrays.asList(new Product(10, "image", "name", "category3", "description", 21.9),
            new Product(11, "image", "name", "category3", "description", 21.9),
            new Product(12, "image", "name", "category3", "description", 21.9));

    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsFirstPage)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsNextPage)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProductsPullToRefresh)));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire intents
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //

    List<FeedItem> expectedDataAfterFristPage =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null)
        );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedDataAfterFristPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Fire next page intent
    //
    robot.fireLoadNextPageIntent();

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page content (plus original first page content)
    //
    List<FeedItem> expectedDataAfterNextPage =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProductsFirstPage.get(0),
            mockProductsFirstPage.get(1),
            mockProductsFirstPage.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null),
            new SectionHeader("category2"),
            mockProductsNextPage.get(0),
            mockProductsNextPage.get(1),
            mockProductsNextPage.get(2),
            new AdditionalItemsLoadable(1, "category2", false, null)
        );

    HomeViewState nextPageLoading =
        new HomeViewState.Builder().data(expectedDataAfterFristPage).nextPageLoading(true).build();
    HomeViewState nextPage = new HomeViewState.Builder().data(expectedDataAfterNextPage).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage);

    //
    // fire pull to refresh intent
    //
    mockWebServer.shutdown(); // Error: no connection to server
    robot.firePullToRefreshIntent();

    //
    // we expect that 6 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator (caused by loadFirstPageIntent)
    // 2. show the items with the first page (caused by loadFirstPageIntent)
    // 3. show loading next page indicator
    // 4. show next page content (plus original first page content)
    // 5. show loading - pull to refresh indicator
    // 6. show error loading  pull to refresh (plus original first page + next page content)
    //
    HomeViewState pullToRefreshLoading = new HomeViewState.Builder().data(expectedDataAfterNextPage)
        .pullToRefreshLoading(true)
        .build();
    HomeViewState pullToRefreshError = new HomeViewState.Builder().data(expectedDataAfterNextPage)
        .pullToRefreshError(new ConnectException())
        .build();

    robot.assertRenderViewState(loadingFirstPage, firstPage, nextPageLoading, nextPage,
        pullToRefreshLoading, pullToRefreshError);
  }

  @Test public void loadingFirstPageAndMoreOfCategory() {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProducts =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    // first page response
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProducts)));

    // more of category responses
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProducts)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire an intent
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //
    List<FeedItem> expectedData =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null)
        );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedData).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Load more of category
    //
    robot.fireLoadAllProductsFromCategory("category1");

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    // 3. show indicator load more of category
    // 4. show all items of the category
    //

    List<FeedItem> expectedDataWhileLoadingMoreOfCategory =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            new AdditionalItemsLoadable(2, "category1", true, null)
        );

    List<FeedItem> expectedDataAfterAllOfCategoryCompleted =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            mockProducts.get(3),
            mockProducts.get(4)
        );

    HomeViewState loadingMoreOfCategory =
        new HomeViewState.Builder().data(expectedDataWhileLoadingMoreOfCategory).build();
    HomeViewState moreOfCategoryLoaded =
        new HomeViewState.Builder().data(expectedDataAfterAllOfCategoryCompleted).build();

    robot.assertRenderViewState(loadingFirstPage, firstPage, loadingMoreOfCategory,
        moreOfCategoryLoaded);
  }

  @Test public void loadingFirstPageAndMoreOfCategoryFails() throws IOException {
    //
    // Prepare mock server to deliver mock response on incoming http request
    //
    List<Product> mockProducts =
        Arrays.asList(new Product(1, "image", "name", "category1", "description", 21.9),
            new Product(2, "image", "name", "category1", "description", 21.9),
            new Product(3, "image", "name", "category1", "description", 21.9),
            new Product(4, "image", "name", "category1", "description", 21.9),
            new Product(5, "image", "name", "category1", "description", 21.9));

    // first page response
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProducts)));

    // more of category responses
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(mockProducts)));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));
    mockWebServer.enqueue(new MockResponse().setBody(adapter.toJson(Collections.emptyList())));

    //
    // init the robot to drive to View which triggers intents on the presenter
    //
    HomePresenter presenter =
        new DependencyInjection().newHomePresenter();   // In a real app you could use dagger or instantiate the Presenter manually like new HomePresenter(...)
    HomeViewRobot robot = new HomeViewRobot(presenter);

    //
    // We are ready, so let's start: fire an intent
    //
    robot.fireLoadFirstPageIntent();

    //
    // we expect that 2 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    //
    List<FeedItem> expectedData =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            new AdditionalItemsLoadable(2, "category1", false, null)
        );

    HomeViewState loadingFirstPage = new HomeViewState.Builder().firstPageLoading(true).build();
    HomeViewState firstPage = new HomeViewState.Builder().data(expectedData).build();

    // Check if as expected
    robot.assertRenderViewState(loadingFirstPage, firstPage);

    //
    // Load more of category
    //
    mockWebServer.shutdown();
    robot.fireLoadAllProductsFromCategory("category1");

    //
    // we expect that 4 view.render() events happened with the following HomeViewState:
    // 1. show loading indicator
    // 2. show the items with the first page
    // 3. show indicator load more of category
    // 4. show loading all items of the category failed
    //

    List<FeedItem> expectedDataWhileLoadingMoreOfCategory =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            new AdditionalItemsLoadable(2, "category1", true, null)
        );

    List<FeedItem> expectedDataAfterLoadingMoreOfCategoryError =
        Arrays.asList(
            new SectionHeader("category1"),
            mockProducts.get(0),
            mockProducts.get(1),
            mockProducts.get(2),
            new AdditionalItemsLoadable(2, "category1", false, new ConnectException())
        );

    HomeViewState loadingMoreOfCategory =
        new HomeViewState.Builder().data(expectedDataWhileLoadingMoreOfCategory).build();
    HomeViewState moreOfCategoryError =
        new HomeViewState.Builder().data(expectedDataAfterLoadingMoreOfCategoryError).build();

    robot.assertRenderViewState(loadingFirstPage, firstPage, loadingMoreOfCategory,
        moreOfCategoryError);
  }
}
