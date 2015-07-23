---
layout: page
title: Writing an App
permalink: /first-app/
---

# Writing an App
_In [Model-View-Presenter fundamentals]({{ site.baseurl }}/mvp/) we have discussed what MVP is and how Mosby implements that. Furthermore, we have explained how [Mosby's ViewState]({{ site.baseurl }}/mvp/) feature work. Now let's put that all together and lets write an App powered on Mosby_

We will write a sample app which mimics a mail client. It's not a real mail client, it's not connected to a real POP3 or IMAP Server. All the data is randomly generated on app start. There is no persistent layer like local sqlite database. The APK file can be downloaded from [here](https://github.com/sockeqwe/mosby/releases/download/1.2.0/sample-mail-debug.apk).

This video shows the app:

<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/_dEYtXgoyBM?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
</p>

Of course the whole app is based on Mosby and as you see in the video above everything keeps it's  state during orientation changes. Regarding the data structure: A `Mail` is linked to a `Person` as sender and another Person as receiver. Every mail is associated to exactly one `Label`. A Label is just something like a "folder". A mail can be assigned to one Label. One Label has arbitrary many mails assigned. A Label is basically just a String. In this sample app there are four labels: "Inbox", "Sent", "Spam" and "Trash". So deleting a mail form inbox is just reassigning the label of the mail from "Inbox" to "Trash".
`MailProvider` is the central business logic element where we can query list of mails and so on. Furthermore, an `AccountManager` exists who is responsible to authenticate a user. As you see in the video on first app start you have to sign in to see the mails. Internally for the business logic (`MailProvider` and `AccountManager`) I use RxJava. If you are not familiar with RxJava, don't worry. All you have to know is, that RxJava offers an `Observable` to do some kind of query and as callback the methods `onData()`, `onCompleted()` or `onError()` gets called. If you are familiar with RxJava, please DON'T look at the code! It's pretty ugly as I changed mind several times during development and didn't refactored everything as I would do for a real app. Please note that this sample app is about Mosby and not how to write a clean business logic with RxJava. Note also that even if I have used Dagger2 for dependency injection this is not a reference app for Dagger2 nor for material design. For sure there is room for improvements and I would gladly accept pull requests for this sample app.

As you see in the video I'm simulating network traffic by adding a delay of two seconds to every request (like loading a list of mails). I also simulate network problems: Every fifth request will fail (that's why you see the error view quite often in the video). Furthermore, I simulate authentication problems: After 15 requests the user has to sign in again (a login button will be dipslayed).


This page describes how to use the `ViewState` feature of Mosby. In [MVP fundamentals]({{ site.baseurl }}/mvp/) we have shown how to implement a simple Fragment that displays a list of Countries loaded by using MVP.

**Question:** What happens if we rotate our device from portrait to landscape that runs our countries example app and already displays a list of countries?

**Answer:** A new `CountriesFragment` gets instantiated and the app starts by showing the `ProgressBar` (and loads list of countries again) rather than displaying the list of countries in the `RecyclerView` (as it was before the screen rotation) as you can see in the video below:

<p>
<iframe width="640" height="480" class="videoContainer" src="https://www.youtube.com/embed/tSRoIwDXidQ?rel=0" frameborder="0" allowfullscreen></iframe>
</p>
Mosby introduces `ViewState` to solve this problem. The idea is that we track the methods the Presenter invokes on the attached `View`. For instance the Presenter calls `view.showContent()`. Once `showContent()` gets called the view knows that it's state is "showing content" and hence the view  stores this information into a `ViewState`. If the view gets destroyed during orientation changes, the ViewState gets stored into a bundle in `Activity.onSaveInstanceState(Bundle)` or `Fragment.onSaveInstanceState(Bundle)` and will be restored in `Activity.onCreate(Bundle)` or `Fragment.onActivityCreated(Bundle)`. There are already some base classes that support `ViewState` you can extend from like `MvpViewStateActivty`, `MvpViewStateFragment`, `MvpViewStateFrameLayout`, `MvpViewStateLinearLayout`, `MvpViewStateRelativeLayout` and also the _LCE_ (Loading-Content-Error) implementation like `MvpLceViewStateFragment` and `MvpLceViewStateActivity`. So let's change our `CountriesFragment` from [MVP fundamentals page]({{ site.baseurl }}/mvp/) to support `ViewState`:

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

   // The code below is the same as with on MVP fundamentals page (without ViewState)

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

As already said before, we use a `ViewState` to keep track which in "state" the view is. With state we mean things like "showing loading spinner", "showing loaded content view", "showing error message". We simply do that by setting the ViewState's internal state as the `View` interface methods `showLoading()`, `showError()` and `showContent()` gets invoked. If the activity or Fragment get's destroyed during screen orientation changes we store the ViewState into the persistent Bundle in `onSaveInstanceState(Bundle)`. We also have to save the data the view is currently displaying. This is why you have to implement `getData()` method. Here you have to return the data that has been set previously with `setData()` (See [MVP fundamentals]({{ site.baseurl }}/mvp/) ) and finally displayed on screen by calling `showContent()`. In order to be able to put the data this View displays into the `Bundle` along with the `ViewState` the data **must** be `Parcelable`. `createViewState()` is the method that gets called internally to instantiate a new ViewState object. There are already some ViewState  implementations for the most use cases that you can use like `ArrayListLceViewState` (to be used when your data is `ArrayList<? extends Parcelable>`) or `CastedArrayListLceViewState` (to be used with `List<? extends Parcelable>`). Check the java docs for implementations.

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

By using retaining Fragments you are able to implement retaining Presenters. This also means that in this case the `ViewState` is not just "faking" that everything is still in the same state as before screen orientation changes, indeed the Presenter survives screen orientation changes and only the portrait View gets detached from Presenter and afterwards the landscape View get attached to the same Presenter instance as in portrait. By using retaining Fragments the data that the View is displays doesn't has to implement `Parcelable` since `ViewState` will be kept in memory (and not put into a `Bundle`) during screen orientation changes. Thus we also don't have to worry about the 1MB data limit as we have to with Activities. For retaining Fragments with an `MvpLceView` (Loading-Content-Error) you should use `RetainingFragmentLceViewState`.

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

  @Override public RestoreableViewState createViewState() {
    return new MyCustomViewState(); // Our ViewState implementation
  }

  // Will be called when no view state exist yet,
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
public class MyCustomViewState implements RestoreableViewState<MyCustomView> {

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

As you can see we have to save our `ViewState` in `saveInstanceState()` which will be called from `Activity.onSaveInstanceState()` and restore the viewstate's data in `restoreInstanceState()` which will be called from `Activity.onCreate()`. The `apply()` method will be called from Activity to restore the view state. We do that by calling the same View interface methods `showA()` or `showB()` like the presenter does.

This external `ViewState` class pulls the complexity and responsibility of restoring the view's state out from the Activity code into an separated class. It's also easier to write unit tests for a `ViewState` class  than for an `Activity` class: Since you can assume that Mosby works correctly and saves and restores your ViewState properly you basically just have to test if `ViewState.apply()` works as expected and invokes the corresponding View methods correctly.

## ViewState Delegate
In the [introduction to MVP page]({{ site.baseurl }}/mvp/) we have already said that Mosby uses delegation and composition to allow you to include Mosbys MVP functionality into any Activity, Fragment or ViewGroup. There is also a delegate to include MVP + ViewState in any Activity, Fragment or ViewGroup.

- `MvpViewStateDelegateCallback`: This interface extends from `MvpDelegateCallback` and defines the method you have to implement like `createViewState()`.
 - `ActivityMvpViewStateDelegateImpl`: This delegate is an extension of `ActivityMvpDelegateImpl` and works exactly the same way as shown in the previous code snippet: you have to call the delegates method from the corresponding activity lifecycle method. Like shown above your custom activity has to implement `MvpViewStateDelegateCallback` and use a `ActivityMvpViewStateDelegateImpl` instead of the non ViewState related ones:
 {% highlight java %}
 public abstract class MyViewStateActivity extends Activity implements MvpViewStateDelegateCallback<> {

   protected ActivityMvpDelegate mvpDelegate = new ActivityMvpViewStateDelegateImpl(this);

   // The rest is still the same as shown above without ViewState support

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

The advantage of delegation is that you can add Mosby MVP and ViewState support to any other `Activity`, `Fragment` or `ViewGroup` not included in the Mosby library like `DialogFragment`. In the main menu of the mail example you see the "statistics" menu item. If you click on it a DialogFragment get's displayed. This is implemented like this:

{% highlight java %}
public class StatisticsDialog extends DialogFragment
   implements StatisticsView, MvpViewStateDelegateCallback<StatisticsView, StatisticsPresenter> {

 @InjectView(R.id.contentView) RecyclerView contentView;
 @InjectView(R.id.loadingView) View loadingView;
 @InjectView(R.id.errorView) TextView errorView;
 @InjectView(R.id.authView) View authView;

 StatisticsPresenter presenter;
 ViewState<StatisticsView> viewState;
 MailStatistics data;
 StatisticsAdapter adapter;

 // Delegate
 private FragmentMvpDelegate<StatisticsView, StatisticsPresenter> delegate =
     new FragmentMvpViewStateDelegateImpl<>(this);


 @Override public void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   delegate.onCreate(savedInstanceState);
 }

 @Override public void onDestroy() {
   super.onDestroy();
   delegate.onDestroy();
 }

 @Override public void onPause() {
   super.onPause();
   delegate.onPause();
 }

 @Override public void onResume() {
   super.onResume();
   delegate.onResume();
 }

 @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
     Bundle savedInstanceState) {
   return inflater.inflate(R.layout.fragment_statistics, container, false);
 }

 @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
   super.onViewCreated(view, savedInstanceState);
   delegate.onViewCreated(view, savedInstanceState);

   ButterKnife.inject(this, view);
   adapter = new StatisticsAdapter(getActivity());
   contentView.setAdapter(adapter);
   contentView.setLayoutManager(new LinearLayoutManager(getActivity()));
 }

 @Override public void onStart() {
   super.onStart();
   delegate.onStart();
 }

 @Override public void onStop() {
   super.onStop();
   delegate.onStop();
 }

 @Override public void onAttach(Activity activity) {
   super.onAttach(activity);
   delegate.onAttach(activity);
 }

 @Override public void onDetach() {
   super.onDetach();
   delegate.onDetach();
 }

 @Override public void onActivityCreated(Bundle savedInstanceState) {
   super.onActivityCreated(savedInstanceState);
   delegate.onActivityCreated(savedInstanceState);
 }

 @Override public void onSaveInstanceState(Bundle outState) {
   super.onSaveInstanceState(outState);
   delegate.onSaveInstanceState(outState);
 }

 ...

}
{% endhighlight %}

Delegation allows you to add functionality to any class inclusive third party frameworks like [RoboGuice](https://github.com/roboguice/roboguice). Mosby's ships with support Fragment (Fragments from support library). With delegation as described above you can use "native" `android.app.Fragment` as well.

Another advantage of delegation is that you can change Mosby's default behavior by implementing a custom Delegate. For example: Mosby's default implementation of how to handle Presenter during orientation changes is to recreate the presenter and to restart the requests (excepted retaining Fragments where the Presenter survives). You could write another `ActivityMvpDelegate` or `FragmentMvpDelegate` that internally uses a `HashMap<Integer, MvpPresenter>` to store an already existing Presenter and reuse it when the View gets recreated after orientation changes (instead of creating a new one and restart requests):

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
   delegateCallback.getPresenter().detachView(true); // true == presenter retains instance state
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

We assign each view a unique id. With this id we can find the presenter in `presenterMap`. If no presenter is available, then we create a new one and put it into the `presenterMap`. The view's unique id is stored into the views bundle. That way you would be able to have retaining presenters for Activities and non retaining Fragments as well. So why this is not the default implementation of Mosby? The problem with that approach is memory management. When does a Presenter gets removed from this map? If Presenter never gets removed does it cause memory leaks? Usually presenters are lightweight objects but most of the time they are a callback for an async running thread. To avoid memory leaks this is not the default strategy in Mosby. You could also implement an  `ActivityMvpDelegate` that stores the Presenters state in a `Bundle` (like Mortar does, in combination with dagger scopes). So you see: Mosby offers a flexible scaffold and a default implementation that match the most scenarios. However, Mosby is customizable for edge cases.
