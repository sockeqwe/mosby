---
layout: page
title: ViewState
permalink: /viewstate/
---

# ViewState
_Before reading this page you should have read the [Model-View-Presenter fundamentals]({{ site.baseurl }}/mvp/)_

This page describes how to use the `ViewState` feature of Mosby. In the [MVP fundamentals]({{ site.baseurl }}/mvp/) we have shown how to implement a simple Fragment that displays a list of Countries loaded by using MVP.

**Question:** What happens if we rotate our device from portrait to landscape that runs our countries example app and already displays a list of countries?

**Answer:** A new `CountriesFragment` gets instantiated and the app starts by showing the `ProgressBar` (and loads list of countries again) rather than displaying the list of countries in the `RecyclerView` (as it was before the screen rotation) as you can see in the video below:

<p>
<iframe width="640" height="480" class="videoContainer" src="https://www.youtube.com/embed/tSRoIwDXidQ?rel=0" frameborder="0" allowfullscreen></iframe>
</p>
Mosby introduces `ViewState` to solve this problem. The idea is that we track the methods the Presenter invokes on the attached `View`. For instance the Presenter calls `view.showContent()`. Once `showContent()` gets called the view knows that it's state is "showing content" and hence the view  stores this information into a `ViewState`.

There are already some base classes that support `ViewState` you can extend from like `MvpViewStateActivty`, `MvpViewStateFragment`, `MvpViewStateFrameLayout`, `MvpViewStateLinearLayout`, `MvpViewStateRelativeLayout` and also the _LCE_ (Loading-Content-Error) implementation like `MvpLceViewStateFragment` and `MvpLceViewStateActivity`. So let's change our `CountriesFragment` from [MVP fundamentals page]({{ site.baseurl }}/mvp/) to support `ViewState`:

{% highlight java %}
public class CountriesFragment
    extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  CountriesAdapter adapter;

  @Override public void onCreate(Bundle savedState){
    super.onCreate(savedState);
    setRetainInstance(true);
  }

  @Override public LceViewState<List<Country>, CountriesView> createViewState() {
    return new RetainingLceViewState<List<Country>, CountriesView>(this);
  }

  @Override public List<Country> getData() {
    return adapter == null ? null : adapter.getCountries();
  }

   // The code below is the same as with on MVP fundamentals page (without ViewState)

   @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     return inflater.inflate(R.layout.countries_list, container, false);
   }

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

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void onRefresh() {
    loadData(true);
  }
}
{% endhighlight %}

We don't have to change code of your presenter or model (business logic). All we have to do is to make `CountryFragment` extend from `MvpLceViewStateFragment` instead of `MvpLceFragment`. Here is a video of our CountriesFragment **with ViewState** support where you can see that now the view is still in the same "state" as before screen orientation changes: the view displays the list of Countries in portrait, then it also displays the list of Countries in landscape. The view shows the pull to refresh indicator in landscape and shows the pull to refresh indicator after changing to portrait as well.
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

As already said before, we use a `ViewState` to keep track which in "state" the view is. With state we mean things like "showing loading spinner", "showing loaded content view", "showing error message". We simply do that by setting the ViewState's internal state as the `View` interface methods `showLoading()`, `showError()` and `showContent()` gets invoked.

`ViewState` is just an interface that providing one method: `ViewState.apply()`. Let's have a look at the default `LceViewState` implementation (simplified):

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

`ViewState` is basically just an object that holds the current `MvpView's` internal state and data required to display the current state.

## How does the ViewState survive screen orientation changes?
What happens with an Fragment (or Activity or ViewGroup) when the user rotates the device from landscape to portrait? The Fragment (or Activity or ViewGroup) gets destroyed and an entirely new Fragment object gets created. What does that mean for the ViewState? For simplicity let's say that there are two Fragments: `PortraitFragment` and `LandscapeFragment`. Let's assume that we start our app in `PortraitFragment` and when the user rotates his device the `PortraitFragment` gets destroyed and the `LandscapeFragment` gets created. A ViewState object was created and referenced from `PortraitFragment`. When `PortraitFragment` gets destroyed all the objects referenced by `PortraitFragment` will be destroyed as well. However, we have to ensure that the ViewState object (refrence) will be kept during screen orientation and be reattached to `LandscapeFragment`.

Doing that with Fragments is easy because Fragments offer a method to turn retaining instance state on: `Fragment.setRetainInstance(true)`. This transforms a Fragment into a "RetainingFragment" which means only the Fragment's GUI (the `android.view.View`  returned from `onCreateView()`) get's destroyed an newly created but all referenced objects (like ViewState) will still be there after screen orientation changes. Mosby offers a similar approach for Activities (by using `onRetainNonConfigurationInstance()`). So the ViewState instance can be moved from `PortraitFragment` to `LandscapeFragment` and `PortraitActivity` to `LandscapeActivity` painlessly because the ViewState object will be kept in memory.

As android is a real multitasking operating system Activities (and containing Fragments and ViewGroups) can be destroyed in background, i.e. when the user starts another activity because of low memory. What does that actually mean for referenced objects? Everything in memeory gets destroyed incl. ViewState. So how do you deal with that in Android? You have to persist your state into a `Bundle` in `Activity.onSaveInstanceState(Bundle bundle)` and restore the state in `Activity.onCreate(Bundle savedState)` (same for Fragments). Hence Mosby offers two kind of ViewState. ViewState itself is a interface and is meant to be kept in memory during screen orientation changes. `RestorableViewState extends ViewState` is a special ViewState that offers you the possibility to serialize and deserialize your ViewState into or from a Bundle. So `RestorableViewState` can be used to "survive" these scenarios when activty gets destroyed in background. But that also means, that the data required for the given view state must be serializeable as well usually by implementing `Parcelable` interface. In our previous example Countries-List example we have to make `Country` class `Parcelable` to put it into a `RestorableViewState`. There are already some RestorableViewState implementations available like ArrayListLceViewState (to be used when your data is ArrayList<? extends Parcelable>) or CastedArrayListLceViewState (to be used with List<? extends Parcelable>). Check the java docs for implementations.

Please note, that a `Bundle` is limited in size by 1MB roughly. That means that you can not put arbitrary big data (i.e. a very very very huge list of Countries) into the ViewState which at the end gets stored into the Bundle. Therefore `RestorableViewState` is not a solution for all use cases, but it should cover the most use cases sufficiently.

Please note also that `setRetainInstance()` is not available for ViewGroups. Hence you have to use `RestorableViewState` in combination with ViewGroups (like MvpViewStateLinearLayout).

## What about the Presenter?
Glad you ask! Presenter faces the same problem as ViewState: Can be kept in memory during screen orientation changes by using `setRetainInstance(true)` but will be destroyed when the activity gets destroyed in background. RestorableViewStates can be serialized into a bundle but Presenter are most of the time connected to the business logic that does something in a background thread. So when the Presenter get's destroyed it will loose the reference to the background thread as well. So the only thing we can do is restart that background thread by re-invoking the corresponding presenter method. To know in which state the presenter was we also use the ViewState. Mosby offers two methods `MvpViewStateActivity.onNewViewState()`, which is called when the Activity start for the first time, and `onViewStateInstanceRestored(boolean retainingInstance)`, when the view state has been restored (true as parameter if it was restored in memory by using `setRetainInstance()`).

In this example workflow we are taking an Activity as View (it works pretty the same way for Fragments) to demonstrate how `isRetainInstance(false)` and `RestorableViewState` work together with the Presenter:

  1. We start our app in portrait
  2. The Activity (View) gets instantiated and calls `onCreate()`, `createPresenter()` and attaches the View (the Activity itself) to the Presenter by calling `presenter.attachView()`. Also `onNewViewState()` gets invoked. This basically means that the Activity is displayed for the first time. Usually you start loading data from this method.
  3. Next we rotate the device from portrait to landscape.
  4. `onDestroy()` gets called which calls `presenter.detachView(false)`. Presenter cancels background tasks.
  5. `onSaveInstanceState(Bundle)` gets called where the `RestorableViewState` gets saved into the Bundle.
  6. App is in landscape now. Instead of switching between portrait and landscape we could also say that the original portrait activity has been destroyed in background (the workflow is the same).  A new Activity gets instantiated and calls `onCreate()` and `createPresenter()`, which creates a new Presenter instance and attaches the new View (Activity itself) to the new Presenter by calling `presenter.attachView()`.
  7. `ViewState` gets restored from Bundle and restores the view's state by calling `ViewState.apply()`. If the `ViewState` was `showLoading()` then the presenter's loading method will be called as well to restart the background business logic thread.

 To sum it up here is a lifecycle diagram for **Activities** with ViewState support, but it's pretty the same for Fragments as well.
 ![Model-View-Presenter]({{ site.baseurl }}/images/mvp-activity-lifecycle.png)

 You may be a little bit disappointed now you know that the Presenter gets destroyed and background work restarted. This might also be critical in certain apps i.e. if you have a sign up view to register a new user you certainly don't want that the registration will be submitted twice to your apps backend. That is not a specific Mosby problem. It's an android problem! How would you deal with such a problem without Mosby? We would recommend to start an android service for submitting the registration. You could attach and reattach your Presenter to that service (business logic). Unlikely with another background thread or AsyncTask you can get a reference to an android service. Hence it's not problematic if the presenter instance gets recreated since presenter can retrieve a reference to the android service.


Now let's have a look at the workflow for simple screen orientaion changes when we use `setRetainInstance(true)`. In that case we just detach the old view from presenter and attach the new view to presenter. As already discussed above by using `setRetainInstance(true)` the presenter and the ViewState will be kept in memory and will not be newly instantiated. Hence the presenter doesn't have to cancel any running background task, because only the view (GUI) gets recreated and  reattached to the presenter. Example:

  1. We start our app in portrait.
  2. The retaining Fragment gets instantiated and calls `onCreate()`, `onCreateView()`, `createPresenter()` and attach the view (the Fragment himself) to the Presenter by calling `presenter.attachView()`. Also `onNewViewState()` gets invoked. This basically means that the Fragment is displayed for the first time. Typically this is the right point to start loading the data.
  3. Next we rotate our device from portrait to landscape.
  4. `onDestroyView()` gets called which calls `presenter.detachView(true)`. Note that the parameter `true` informs the Presenter the Presenter will be retained and a new view will be attached afterwards (otherwise the parameter would be set to false). Therefore the Presenter knows that he doesn't have to cancel running background threads.
  5. App is in landscape now. `onCreateView()` gets called, but **not** `createPresenter()` because  `presenter != null` since Presenter variable has survived orientation changes because of `setRetainInstanceState(true)`.
  6. View gets reattached to Presenter by `presenter.attachView()`.
  7. `ViewState` gets restored. Since no background thread has been canceled re-invoking presenters load method to restart background thread is not needed.

The lifecycle diagram for **Fragments** looks like this:

![Retaining Fragment lifecycle]({{ site.baseurl }}/images/mvp-fragment-lifecycle.png)

You could also mix both scenarios and solitions: You can `setRetainInstance(true)` and use a `RestorableViewState`. That means that during simple orientation changes ViewState and Presenter will be kept in memory, but when the Activity gets destroyed in background i.e. low memory the ViewState will be stored into the bundle and deserialized when coming back to the destroyed activity, which then will force the presenter to restart his work. This sounds like the perfect solution for all the problems, doesn't it? The problem is that you have to respect all the limitations of `RestoreabelViewState` like data must be `Parcelable`, Bundle limit of 1MB and so on. So it's up to you to decide which solution to implement in your app (RestorableViewState or not). Maybe you don't need the ViewState feature anyway?

## Custom ViewState
`ViewState` is a really powerful and flexible concept. So far you learned how easy it is to use one of the provided LCE (Loading-Content-Error) ViewsStates. Now lets write our own custom View and ViewState. Our View should only display two different kind of data objects `A` and `B`. The result should look like this:
<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/9iSBGEIZmUw?rel=0" frameborder="0" allowfullscreen class="videoContainer" ></iframe>
</p>

It's not that impressive. It should just give you an idea of how easy it is to create your own ViewState.

The View interface and the data objects (model) looks like this:
{% highlight java %}

public class A implements Parcelable {
  String name;

  public A(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

public class B implements Parcelable {
  String foo;

  public B(String foo) {
    this.foo = foo;
  }

  public String getFoo() {
    return foo;
  }
}

public interface MyCustomView extends MvpView {

  public void showA(A a);

  public void showB(B b);
}
{% endhighlight %}

We don't have any business logic in this simple sample. Let's assume that in a real world app there would be a complex operation in our business logic to generate `A` or `B`. Our presenter looks like this:

{% highlight java %}
public class MyCustomPresenter extends MvpBasePresenter<MyCustomView> {

  Random random = new Random();

  public void doA() {

    A a = new A("My name is A "+random.nextInt(10));

    if (isViewAttached()) {
      getView().showA(a);
    }
  }

  public void doB() {

    B b = new B("I am B "+random.nextInt(10));

    if (isViewAttached()) {
      getView().showB(b);
    }
  }
}
{% endhighlight %}

We define `MyCustomActivity` which implements `MyCustomView`

{% highlight java %}
public class MyCustomActivity extends MvpViewStateActivity<MyCustomView, MyCustomPresenter>
    implements MyCustomView {

  @InjectView(R.id.textViewA) TextView aView;
  @InjectView(R.id.textViewB) TextView bView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.my_custom_view);
  }

  @Override public ViewState<MyCustomView> createViewState() {
    return new MyCustomViewState(); // Our ViewState implementation
  }

  // Will be called when no view state exists yet,
  // which is the case the first time MyCustomActivity starts
  @Override public void onNewViewStateInstance() {
    presenter.doA();
  }

  @Override protected MyCustomPresenter createPresenter() {
    return new MyCustomPresenter();
  }

  @Override public void showA(A a) {
    MyCustomViewState vs = ((MyCustomViewState) viewState);
    vs.setShowingA(true);
    vs.setData(a);
    aView.setText(a.getName());
    aView.setVisibility(View.VISIBLE);
    bView.setVisibility(View.GONE);
  }

  @Override public void showB(B b) {
    MyCustomViewState vs = ((MyCustomViewState) viewState);
    vs.setShowingA(false);
    vs.setData(b);
    bView.setText(b.getFoo());
    aView.setVisibility(View.GONE);
    bView.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.loadA) public void onLoadAClicked() {
    presenter.doA();
  }

  @OnClick(R.id.loadB) public void onLoadBClicked() {
    presenter.doB();
  }
}
{% endhighlight %}

Since we are not having LCE (Loading-Content-Error) we are not using `MvpLceActivity` as base class. We use `MvpViewStateActivity` as base class which is the most general Activity implementation that supports `ViewState`. Basically our View simply displays `aView` or `bView`.
In `onNewViewStateInstance() ` we have to specify what to do on first Activity start, because no previous `ViewState` instance exists to restore. In `showA(A a)` and `showB(B b)` we have to save the information that we are displays `A` or `B` into our `ViewState`. We are almost done, only  `MyCustomViewState` implementation is missing:

{% highlight java %}
public class MyCustomViewState implements RestorableViewState<MyCustomView> {

  private final String KEY_STATE = "MyCustomViewState-flag";
  private final String KEY_DATA = "MyCustomViewState-data";

  public boolean showingA = true; // if false, then show B
  public Parcelable data; // Can be A or B

  @Override public void saveInstanceState(Bundle out) {
    out.putBoolean(KEY_STATE, showingA);
    out.putParcelable(KEY_DATA, data);
  }

  @Override public boolean restoreInstanceState(Bundle in) {
    if (in == null) {
      return false;
    }

    showingA = in.getBoolean(KEY_STATE, true);
    data = in.getParcelable(KEY_DATA);
    return true;
  }

  @Override public void apply(MyCustomView view, boolean retained) {

    if (showingA) {
      view.showA((A) data);
    } else {
      view.showB((B) data);
    }
  }

  /**
   * @param a true if showing a, false if showing b
   */
  public void setShowingA(boolean a) {
    this.showingA = a;
  }

  public void setData(Parcelable data){
    this.data = data;
  }
}
{% endhighlight %}

As you can see we have to save our `ViewState` in `saveInstanceState()` which will be called from `Activity.onSaveInstanceState()` and restore the ViewState's data in `restoreInstanceState()` which will be called from `Activity.onCreate()`. The `apply()` method will be called from Activity to restore the view state. We do that by calling the same View interface methods `showA()` or `showB()` like the presenter does.

This external `ViewState` class pulls the complexity and responsibility of restoring the view's state out from the Activity code into an separated class. It's also easier to write unit tests for a `ViewState` class  than for an `Activity` class: Since you can assume that Mosby works correctly and saves and restores your ViewState properly you basically just have to test if `ViewState.apply()` works as expected and invokes the corresponding View methods correctly.

## ViewState Delegate
In the [MVP fundamentals page]({{ site.baseurl }}/mvp/) we have already said that Mosby uses delegation and composition to allow you to bring Mosbys MVP functionality into any Activity, Fragment or ViewGroup. Guess what, there is also a delegate to include MVP + ViewState in any Activity, Fragment or ViewGroup as well:

 - `MvpViewStateDelegateCallback`: This interface extends from `MvpDelegateCallback` and defines  ViewState related methods you have to implement like `createViewState()`.
 - `ActivityMvpViewStateDelegateImpl`: This delegate is an extension of `ActivityMvpDelegateImpl`. You have to call the delegates method from the corresponding activity lifecycle method. Furthermore your custom activit has to implement `MvpViewStateDelegateCallback` and use a `ActivityMvpViewStateDelegateImpl` like this:
 {% highlight java %}
 public abstract class MyViewStateActivity extends Activity implements MvpViewStateDelegateCallback<> {

   protected ActivityMvpDelegate mvpDelegate = new ActivityMvpViewStateDelegateImpl(this);

   @Override protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     mvpDelegate.onCreate(savedInstanceState);
   }

   @Override protected void onDestroy() {
     super.onDestroy();
     mvpDelegate.onDestroy();
   }

   @Override protected void onSaveInstanceState(Bundle outState) {
     super.onSaveInstanceState(outState);
     mvpDelegate.onSaveInstanceState(outState);
   }

   ... // other lifecycle methods
 }
 {% endhighlight %}

 - `FragmentMvpViewStateDelegateImpl`: Extension from `FragmentMvpDelegateImpl` to add ViewState support.
 - `ViewGroupMvpViewStateDelegateImpl`: The delegate for `ViewGroups` like FrameLayout to add ViewState support.

As already said, by using the delegate you can support `Activity`, `Fragment` or `ViewGroup` not included in the Mosby library like `DialogFragment` or support third party frameworks like [RoboGuice](https://github.com/roboguice/roboguice). Mosby's ships with support Fragment (Fragments from support library). With delegation as described above you can use "native" `android.app.Fragment` as well.

Another advantage of delegation is that you can change Mosby's default behavior by implementing a custom Delegate. For example: Mosby's default implementation of how to handle Presenter during orientation changes is to recreate the presenter and to restart the requests (excepted `setRetainInstance(true)`). You could write another `ActivityMvpDelegate` or `FragmentMvpDelegate` that internally uses a `HashMap<Integer, MvpPresenter>` to store an already existing Presenter and reuse it when the View gets recreated after orientation changes (instead of creating a new one and restart async. background work):

{% highlight java %}
public interface IdBasedMvpView extends MvpView {
 public void setId(int id);
 public int getId();
}
{% endhighlight %}

{% highlight java %}
public class IdBasedActivityMvpDelegate<V extends IdBasedMvpView, P extends MvpPresenter<V>>
   implements ActivityMvpDelegate {

 // Every view gets an unique id
 private static int lastViewId = 0;

 // Map to store presenter
 private static Map<Integer, MvpPresenter> presenterMap = new HashMap<>();

 // The reference to the Activity
 private MvpDelegateCallback<V, P>  delegateCallback;


 public ActivityMvpDelegateImpl(MvpDelegateCallback<V, P> delegateCallback) {
   this.delegateCallback = delegateCallback;
 }

 // Called from Activity.onCreate()
 @Override
 public void onCreate(Bundle bundle) {

   // Returns 0 if no view id is assigned
   int viewId = bundle.getInt("MvpViewId");

   MvpPresenter presenter = presenterMap.get(viewId);

   if (presenter == null){
     // No presenter in Map --> View starting first time
     // so create a new presenter and put it into presenterMap
     presenter = delegateCallback.createPresenter();

     viewId = ++lastViewId;
     presenterMap.put(viewId, presenter);
   }

   V view = delegateCallback.getMvpView();

   view.setViewId(viewId);
   view.setPresenter(presenter);
   presenter.attachView(view);
 }

 @Override
 public void onSaveInstanceState(Bundle outState) {
   int viewId = delegateCallback.getMvpView().getViewId();
   outState.putInt("MvpViewId", viewId);
 }

 @Override
 public void onDestroy() {
   delegateCallback.getPresenter().detachView(true); // true = presenter retains instance state
 }
}
{% endhighlight %}

{% highlight java %}
public MyActivity extends MvpActivity implements IdBasedMvpView {

 ActivityMvpDelegate mvpDelegate;

 @Override
 protected ActivityMvpDelegate<V, P> getMvpDelegate() {
   if (mvpDelegate == null) {
     mvpDelegate = new IdBasedActivityMvpDelegate(this);
   }

   return mvpDelegate;
 }
}
{% endhighlight %}

The idea is that we assign each view a unique id. With this id we can find the corresponding Presenter in `presenterMap`. If no Presenter is available, then we create a new one and put it into the `presenterMap`. The view's unique id is stored into the views bundle. That way you would be able to have retaining Presenters for Activities and non retaining Fragments as well. So why this is not the default implementation of Mosby? The problem with that approach is memory management. When does a Presenter gets removed from this map? If Presenter never gets removed does it cause memory leaks? Usually presenters are lightweight objects but most of the time they are a callback for an async running thread. To avoid memory leaks this is not the default strategy in Mosby. You could also implement an  `ActivityMvpDelegate` that stores the Presenters state in a `Bundle` (like Mortar does, in combination with dagger scopes). So you see: Mosby offers a flexible scaffold and a default implementation that match the most scenarios. However, Mosby is customizable for edge cases.
