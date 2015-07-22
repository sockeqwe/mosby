---
layout: page
title: ViewState
permalink: /viewstate/
---

# ViewState
_Before reading this page you should have read the [Model-View-Presenter fundamentals]({{ site.baseurl }}/mvp/)_

This page describes how to use the `ViewState` feature of Mosby. In the [previous page]({{ site.baseurl }}/mvp/) we have shown how to implement a simple Fragment that displays a list of Countries loaded by using MVP.

**Question:** What happens if we rotate our device from portrait to landscape that runs our countries example app and already displays a list of countries?

**Answer:** A new `CountriesFragment` gets instantiated and the app starts by showing the `ProgressBar` (and loads list of countries again) rather than displaying the list of countries in the `RecyclerView` (as it was before the screen rotation) as you can see in the video below:

<p>
<iframe width="640" height="480" class="videoContainer" src="https://www.youtube.com/embed/tSRoIwDXidQ?rel=0" frameborder="0" allowfullscreen></iframe>
</p>
Mosby introduces `ViewState` to solve this problem. The idea is that we track the methods the Presenter invokes on the attached `View`. For instance the Presenter calls `view.showContent()`. Once `showContent()` gets called the view knows that it's state is "showing content" and hence the view  stores this information into a `ViewState`. If the view gets destroyed during orientation changes, the ViewState gets stored into a bundle in `Activity.onSaveInstanceState(Bundle)` or `Fragment.onSaveInstanceState(Bundle)` and will be restored in `Activity.onCreate(Bundle)` or `Fragment.onActivityCreated(Bundle)`. There are already some base classes that support `ViewState` you can extend from like `MvpViewStateActivty`, `MvpViewStateFragment`, `MvpViewStateFrameLayout`, `MvpViewStateLinearLayout`, `MvpViewStateRelativeLayout` and also the _LCE_ (Loading-Content-Error) implementation like `MvpLceViewStateFragment` and `MvpLceViewStateActivity`. So let's change our `CountriesFragment` from [previous page]({{ site.baseurl }}/mvp/) to support `ViewState`:

{% highlight java %}
public class CountriesFragment
    extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;
  CountriesAdapter adapter;


  @Override public LceViewState<List<Country>, CountriesView> createViewState() {
    return new CastedArrayListLceViewState<List<Country>, CountriesView>(this);
  }

  @Override public List<Country> getData() {
    return adapter == null ? null : adapter.getCountries();
  }

   // The code below is the same as with on previous page (without ViewState)

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstance) {
    super.onViewCreated(view, savedInstance);

    // Setup contentView == SwipeRefreshView
    contentView.setOnRefreshListener(this);

    // Setup recycler view
    adapter = new CountriesAdapter(getActivity());
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);
    loadData(false);
  }


  public void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  @Override protected CountriesPresenter createPresenter() {
    return new SimpleCountriesPresenter();
  }

  // Just a shorthand that will be called in onCreateView()
  @Override protected int getLayoutRes() {
    return R.layout.countries_list;
  }

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void onRefresh() {
    loadData(true);
  }
}
{% endhighlight %}

We don't have to change code of your presenter. All we have to do is to make `CountryFragment` extend from `MvpLceViewStateFragment` instead of `MvpLceFragment`. Here is a video of our CountriesFragment **with ViewState** support where you can see that now the view is still in the same "state" as before screen orientation changes: the view displays the list of Countries in portrait, then it also displays the list of Countries in landscape. The view shows the pull to refresh indicator in landscape and shows the pull to refresh indicator after changing to portrait as well.
<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/Ni7e5NhUEUw?rel=0"  class="videoContainer" frameborder="0" allowfullscreen></iframe>
</p>

So how does that works? Let's have a look at the actual `MvpLceViewStateFragment` implementation:

{% highlight java %}
public abstract class MvpLceViewStateFragment extends MvpLceFragment {

  protected ViewState<M, V> viewState;

  public abstract ViewState<M, V> createViewState();

  public abstract M getData();

  @Override public void showContent() {
    super.showContent();
    viewState.setStateShowContent(getData());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    viewState.setStateShowError(e, pullToRefresh);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    viewState.setStateShowLoading(pullToRefresh);
  }

}
{% endhighlight %}

As already said before, we use a `ViewState` to keep track which in "state" the view is. With state we mean things like "showing loading spinner", "showing loaded content view", "showing error message". We simply do that by setting the ViewState's internal state as the `View` interface methods `showLoading()`, `showError()` and `showContent()` gets invoked. If the activity or Fragment get's destroyed during screen orientation changes we store the ViewState into the persistent Bundle in `onSaveInstanceState(Bundle)`. We also have to save the data the view is currently displaying. This is why you have to implement `getData()` method. Here you have to return the data that has been set previously with `setData()` (See [previous page]({{ site.baseurl }}/mvp/) ) and finally displayed on screen by calling `showContent()`. In order to be able to put the data this View displays into the `Bundle` along with the `ViewState` the data **must** be `Parcelable`. `createViewState()` is the method that gets called internally to instantiate a new ViewState object. There are already some ViewState  implementations for the most use cases that you can use like `ArrayListLceViewState` (to be used when your data is `ArrayList<? extends Parcelable>`) or `CastedArrayListLceViewState` (to be used with `List<? extends Parcelable>`). Check the java docs for implementations.

`ViewState` is just an interface that provides an API to store itself into a Bundle and to restore itself from a Bundle with `ViewState.saveInstanceState(Bundle)` and `ViewState.restoreInstanceState(Bundle)` respectively. Please note that `restoreInstanceState(Bundle)` just means reading the previous state out of the bundle and not apply the last known state to the corresponding `MvpView`. That job is done in `ViewState.apply()`. Let's have a look at the default `LceViewState` implementation (simplified):

{% highlight java %}
public abstract class LceViewState<D, V extends MvpLceView<D>> implements LceViewState<D, V> {

  /**
    * Used to indicate that loading is currently displayed by the View
    */
   int STATE_SHOW_LOADING = 0;
   /**
    * Used to indicate that content is currently displayed by the View
    */
   int STATE_SHOW_CONTENT = 1;
   /**
    * Used to indicate that the error message is currently displayed by the view
    */
   int STATE_SHOW_ERROR = -1;

  /**
   * The current viewstate. Used to identify if the view is/was showing loading, error, or content.
   */
  protected int currentViewState;
  protected boolean pullToRefresh;
  protected Throwable exception;
  protected D loadedData;

  @Override public void setStateShowContent(D loadedData) {
    currentViewState = STATE_SHOW_CONTENT;
    this.loadedData = loadedData;
  }

  @Override public void setStateShowError(Throwable e, boolean pullToRefresh) {
    currentViewState = STATE_SHOW_ERROR;
    exception = e;
    this.pullToRefresh = pullToRefresh;
  }

  @Override public void setStateShowLoading(boolean pullToRefresh) {
    currentViewState = STATE_SHOW_LOADING;
    this.pullToRefresh = pullToRefresh;
    exception = null;
  }

  @Override public void apply(V view, boolean retained) {

    if (currentViewState == STATE_SHOW_CONTENT) {
      view.setData(loadedData);
      view.showContent();
    } else if (currentViewState == STATE_SHOW_LOADING) {
      showLoading(pullToRefresh);
    } else if (currentViewState == STATE_SHOW_ERROR) {
      view.showError(exception, pullToRefresh);
    }
  }
}
{% endhighlight %}

We hope you get the point: a `ViewState` is basically just an object that holds the current `MvpViews` internal state and persists that into a Bundle and retrieve the internal state out of that Bundle after screen orientation changes. `ViewState.apply()` gets called internally to actually apply the ViewState to the corresponding MvpView (see chapter Delegation below).

Please note, that a `Bundle` is limited in size by 1MB roughly. That means that you can not put arbitrary big data (i.e. a very very very huge list of Countries) into the ViewState which at the end gets stored into the Bundle. Therefore `ViewState` is not a solution for all use cases, but it should cover the most use cases sufficiently. Another solution is to use retaining Fragments (discussed later).

## What about the Presenter?
Glad you asked. So what happens during a screen orientation change is that the MvpView (may be an Activity, Fragment or ViewGroup) gets destroyed. Per default the Presenter get's destroyed as well. So the solution of ViewState discussed so far is just to give the user of your app the impression that nothing has changed during orientation changes. What actually happens behind the scenes is that the View and Presenter gets destroyed, hence the Presenter should cancel running background tasks. In this example workflow we are taking an Activity as View, but it works pretty the same way for Fragments:

  1. We start our app in portrait
  2. The Activity (View) gets instantiated and calls `onCreate()`, `createPresenter()` and attaches the View (the Activity itself) to the Presenter by calling `presenter.attachView()`. Also `onNewViewState()` gets invoked. This basically means that the Activity is displayed for the first time. Usually you start loading data from this method.
  3. Next we rotate the device from portrait to landscape.
  4. `onDestroy()` gets called which calls `presenter.detachView(false)`. Presenter cancels background tasks.
  5. `onSaveInstanceState(Bundle)` gets called where the `ViewState` gets saved into the Bundle.
  6. App is in landscape now. A new Activity gets instantiated and calls `onCreate()` and `createPresenter()`, which creates a new Presenter instance and attaches the new View (Activity itself) to the new Presenter by calling `presenter.attachView()`.
  7. `ViewState` gets restored from Bundle and restores the view's state by calling `ViewState.apply()`. If the `ViewState` was `showLoading()` then the presenter restarts  background threads.

 To sum it up here is a lifecycle diagram for **Activities** with ViewState support, but it's pretty the same for Fragments as well.
 ![Model-View-Presenter]({{ site.baseurl }}/images/mvp-activity-lifecycle.png)

 You may be a little bit disappointed now you know that the Presenter gets destroyed and background work restarted. Understandable, but see it from the bright side: What you get with the solution discussed so far is a cleaner way to handle screen orientation changes like you don't have to save the views state by hand into a bundle and thing about canceling and restarting background tasks etc. but yes, you are still at the same point as without ViewState except the fact that we give the user the impression that everything has survived orientation changes. Don't be afraid, Mosby provides a real solution how Presenters can survive screen orientation changes described in the next chapter.

## Retaining Fragments - Retaining Presenter
What is a retaining Fragments? If you set `Fragment.setRetainInstanceState(true)` then the Fragment will not be destroyed during screen rotations. Only the Fragment's GUI (the `android.view.View`  returned from `onCreateView()`) get's destroyed an newly created. That means all of your fragment class member variables are still there after screen rotation and so is the presenter still there after screen orientation has been changed. In that case we just detach the old view from presenter and attach the new view to presenter. Hence the presenter doesn't have to cancel any running background task, because the view gets reattached. Example:

  1. We start our app in portrait.
  2. The retaining Fragment gets instantiated and calls `onCreate()`, `onCreateView()`, `createPresenter()` and attach the view (the Fragment himself) to the Presenter by calling `presenter.attachView()`. Also `onNewViewState()` gets invoked. This basically means that the Fragment is displayed for the first time. Typically this is the right point to start loading the data.
  3. Next we rotate our device from portrait to landscape.
  4. `onDestroyView()` gets called which calls `presenter.detachView(true)`. Note that the parameter `true` informs the Presenter the Presenter will be retained and a new view will be attached afterwards (otherwise the parameter would be set to false). Therefore the Presenter knows that he doesn't have to cancel running background threads.
  5. App is in landscape now. `onCreateView()` gets called, but **not** `createPresenter()` because  `presenter != null` since Presenter variable has survived orientation changes because of `setRetainInstanceState(true)`.
  6. View gets reattached to Presenter by `presenter.attachView()`.
  7. `ViewState` gets restored. Since no background thread has been canceled restarting background threads is not needed.

The lifecycle diagram for **Fragments** looks like this:

![Retaining Fragment lifecycle]({{ site.baseurl }}/images/mvp-fragment-lifecycle.png)
