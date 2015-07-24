---
layout: page
title: MVP
permalink: /mvp/
---

# Model-View-Presenter
_This page describes the principle of **M**odel-**V**iew-**P**resenter (MVP) and how to use Mosby to create MVP based applications._

* The **model** is the data that will be displayed in the view (user interface).
* The **view** is an interface that displays data (the model) and routes user commands (events) to the presenter to act upon that data. The view usually  has a reference to its presenter.
* The **presenter** is the "middle-man" (played by the controller in MVC) and has references to both, view and model. **Please note that the word "Model"** is misleading. It should rather be **business logic that retrieves or manipulates a Model** For instance: If you have a database with `User` and your View wants to display a list of User, then the Presenter would have a reference to your database business logic (like a DAO) from where the presenter will query a list of Users.


![Model-View-Presenter]({{ site.baseurl }}/images/mvp-overview.png)

A concrete workflow of querying and displaying a list of users from a database could work like this:

![Model-View-Presenter]({{ site.baseurl }}/images/mvp-workflow.png)

The workflow Image shown above should be self-explaining. However here are some additional thoughts:

* The `Presenter` is not a `OnClickListener`. The `View` is responsible for handling user input and invoking the corresponding method of the presenter. Why not eliminating this "forwarding" process by making the `Presenter` an `OnClickListener`? If doing so the presenter needs to have knowledge about views internals. For instance, if a View has two buttons and the view registers the `Presenter` as `OnClickListener` on both, how could the `Presenter` distinguish which button has been clicked on a click event (without knowing views internals like the references to the button)? Model, View and Presenter should be decoupled.
Furthermore, by letting `Presenter` implement `OnClickListener` the Presenter is bound to the android platform. In theory the presenter and business logic could be plain old java code, which could be shared with a desktop application or any other java application.
* The `View` is only doing what the `Presenter` tells the `View` to do like you can see in step 1 and step 2: After the user has clicked on the _"load user button"_ (step 1) the view doesn't show the loading animation directly. It's the presenter (step 2) who explicitly tells the view to show the loading animation. This variant of Model-View-Presenter is called **Passive View**. The view should be as dumb as possible. Let the presenter control the view in an abstract way. For instance: presenter invokes `view.showLoading()` but presenter should not control view specific things like animations. So presenter should not invoke methods like `view.startAnimation()` etc.
* By implementing MVP Passive View it's much easier to handle concurrency and multithreading. Like you can see in step 3 the database query runs async an the presenter is a Listener / Observer and gets notified when data is ready to display.

## MVP on Android
So far so good. but how do you apply MVP on your own Android app? The first question is, where should we apply the MVP pattern? On an Activity, Fragment or a ViewGroup like a RelativeLayout?
Let's have a look at the Gmail Android tablet app:

![Model-View-Presenter]({{ site.baseurl }}/images/mvp-gmail.png)

From our point of view, there are 4 independent MVP candidates on the screen. With MVP candidate we mean UI element(s) displayed on the screen that logically belongs together and therefore can be seen as a single UI unit where we could apply MVP.

![Model-View-Presenter]({{ site.baseurl }}/images/mvp-gmail-candidates.png)

It seems that Activities and especially Fragments are good candidates. Usually a Fragment is responsible to just display a single content like a ListView. For example `InboxView`, controlled by an `InboxPresenter` which uses `MailProvider` to get a List of `Mails`. However, MVP is not limited to Fragments or Activities. You can also apply this design Pattern on `ViewGroups` like shown in `SearchView`. In many apps Fragments are good MVP candidates. However it's up to you to find MVP candidates. Just ensure that the view is independent so that one presenter can control that View without getting in conflict with another Presenter.

## Why should you implement MVP?
How would you implement the inbox view in a traditional Fragment without MVP to display a list of emails that needs to be merged from two sources like a local sql database (on your device) and an IMAP mail server connected over internet. How would your code of the fragment looks like? You would start two `AsyncTasks` and have to implement a "wait mechanism" (wait until both tasks have finished) to merged the loaded data of both tasks to a single list of mails. You also have to take care of displaying a loading animation (ProgressBar) while loading and replace that one with a ListView afterwards. Would you put all that code into the Fragment? What about errors while loading? What about screen orientation changes? Who is responsible to cancel `AsyncTasks`? This kind of problems can be addressed and solved with MVP. Say goodbye to activities and fragments with 1000+ lines of spaghetti code.

But before we dive deeper in how to implement MVP on Android we have to clarify if an Activity or Fragment is a `View` or a `Presenter`. Activity and Fragment seems to be both, because they have lifecycle callbacks like `onCreate()` or `onDestroy()` as well as responsibilities of View things like switching from one UI widget to another UI widget (like showing a ProgressBar while loading and then displaying a ListView with data). You may say that these sounds like an Activity or Fragment is a Controller. However, we came to the conclusion that Activity and Fragment should be treated like as part of a (dumb) **View and not as a Presenter**. You will see why afterwards.

With that said, we want to introduce `Mosby` a library for creating MVP based apps on android.

# Mosby
Some people find it hard to understand what the Presenter exactly is if you try to explain that MVP is a variant or enhancement of MVC (Model-View-Controller). Especially iOS developer having a hard time to understand the difference of Controller and Presenter because they "grew up" with the fixed idea and definition of an iOS alike `UIViewController`. From our point of view MVP is not a variant or enhancement of MVC because that would mean that the Controller gets replaced by the Presenter. In our opinion MVP wraps around MVC. Take a look at your MVC powered app. Typically you have your View and a Controller (i.e. a `Fragment` on Android  or `UIViewController` on iOS) which handles click events, binds data and observers ListView (or implements a `UITableViewDelegate` for `UITableView` on iOS) and so on. If you have this picture in mind now take a step back and try to imagine that the controller is part of the view and not connected directly to your model (business logic). The presenter sits in the middle between controller and model like this:

 ![MVP with Controller]({{ site.baseurl }}/images/mvp-controller.png)

Let's have a look at a concrete example: The example app displays a list of users queried from a database. The action starts when the user clicks on the "load button". While querying the database (async) a `ProgressBar` is displayed and a `ListView` with the queried items afterwards.

 In our opinion the **Presenter does not replace the Controller.** Rather the Presenter coordinates  or supervises the View which the Controller is part of. The Controller is the component that handles the click events and calls the corresponding Presenter methods. The Controller is the responsible component to control animations like hiding ProgressBar and displaying ListView instead. The Controller is listening for scroll events on the ListView i.e. to do some parallax item animations or scroll the toolbar in and out while scrolling the ListView. So all that UI related stuff still gets controlled by a Controller and **not by a Presenter** (i.e. Presenter should not be an OnClickListener). The presenter is responsible to coordinate the overall state of the view layer (composed of UI widgets and Controller). So it's the job of the presenter to tell the view layer that the loading animation should be displayed now or that the ListView should be displayed because the data is ready to be displayed.

## MosbyActivity and MosbyFragment
`MosbyActivity` and `MosbyFragment` are the base classes (the fundament) for all other activity or fragment subclasses. Both use well known [annotation processors](http://hannesdorfmann.com/annotation-processing/annotationprocessing101/) to reduce writing boilerplate code. _MosbyActivity_ and _MosbyFragment_ use [Butterknife](http://jakewharton.github.io/butterknife/) for view "binding", [Icepick](https://github.com/frankiesardo/icepick) for saving and restoring instance state to a bundle and [FragmentArgs](https://github.com/sockeqwe/fragmentargs) for injecting Fragment arguments. You don't have to call the binding methods like `Butterknife.bind(this)`. This kind of code is already included in _MosbyActivity_ and _MosbyFragment_. It just works out of the box. The only thing you have to do is to use the corresponding annotations in your subclasses.


## MvpView and MvpPresenter
The base class for all views is `MvpView`. Basically it's just an empty interface. Later on this page you will see that you define methods for your custom view in your own interface that extends from `MvpView`. The interface provides a public API for the Presenter to invoke View related methods. The base class for presenters is `MvpPresenter`:

{% highlight java %}
public interface MvpView { }


public interface MvpPresenter<V extends MvpView> {

  public void attachView(V view);

  public void detachView(boolean retainInstance);
}
{% endhighlight %}

The idea is that a `MvpView` (i.e. Fragment or Activity) gets attached to and detached from his `MvpPresenter`. Mosby takes Activities and Fragments lifecycle for doing so (more about that below in "Delegate" section). So initializing and cleaning up things (like canceling async running tasks) should be done in `presenter.onAttach()` and `presenter.onDetach()`.

Mosby provides `MvpBasePresenter`, a presenter implementation which uses `WeakReference` to hold the reference to the view (which is a Fragment or Activity) to avoid memory leaks. Therefore when your presenter wants to invoke a method of the view you always have to check if the view is attached to the presenter by checking `isViewAttached()` and using `getView()` to get the reference.  
Alternatively, you could use `MvpNullObjectBasePresenter` class that implements [Null Object Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) for the `MvpView`. So whenever `MvpNullObjectBasePresenter.onDetach()` gets called the View will not be set to `null` (as `MvpBasePresenter` does. Instead an empty View gets created dynamically by using reflections and gets attached as view to the presenter. This avoids `view != null` checks since either the real View is attached or the null object pattern view is attached that simply does nothing on method invocation.

## MvpActivity and MvpFragment
As already mentioned before we treat Activities and Fragments as Views. `MvpActivity` and `MvpFragment` implements `MvpView` can be used as base classes in your application if you simply want an Activity or Fragment that gets controlled by an Presenter. To ensure type safety generics are used: `MvpActivity<V extends MvpView, P extends MvpPresenter>` and `MvpFragment<V extends MvpView, P extends MvpPresenter>`  

## Loading-Content-Error (LCE)
Often you find yourself writing the same things in an app over and over again: Load data in background, display a loading view (i.e ProgressBar) while loading, display the loaded data on screen or display an error message if loading has failed. Nowadays supporting pull to refresh is easy as `SwipeRefreshLayout` is part of android's support library. To not reimplementing this workflow again and again Mosby provides `MvpLceView`:

{% highlight java %}
/**
 * @param <M> The type of the data displayed in this view
 */
public interface MvpLceView<M> extends MvpView {

  /**
   * Display a loading view while loading data in background.
   * <b>The loading view must have the id = R.id.loadingView</b>
   *
   * @param pullToRefresh true, if pull-to-refresh has been invoked loading.
   */
  public void showLoading(boolean pullToRefresh);

  /**
   * Show the content view.
   *
   * <b>The content view must have the id = R.id.contentView</b>
   */
  public void showContent();

  /**
   * Show the error view.
   * <b>The error view must be a TextView with the id = R.id.errorView</b>
   *
   * @param e The Throwable that has caused this error
   * @param pullToRefresh true, if the exception was thrown during pull-to-refresh, otherwise
   * false.
   */
  public void showError(Throwable e, boolean pullToRefresh);

  /**
   * The data that should be displayed with {@link #showContent()}
   */
  public void setData(M data);
}
{% endhighlight %}

You can use `MvpLceActivity implements MvpLceView` and `MvpLceFragment implements MvpLceView` for that kind of view. Both assume that the inflated xml layout contains views with `R.id.loadingView`, `R.id.contentView` and `R.id.errorView`.


### Example
In the following example (hosted on [Github](https://github.com/sockeqwe/mosby/tree/master/sample) ) we are loading a list of `Country` by using `CountriesAsyncLoader` and display that in a `RecyclerView` in a Fragment.

Let's start by defining the view interface `CountriesView`:
{% highlight java %}
public interface CountriesView extends MvpLceView<List<Country>> {
}
{% endhighlight %}

Why do I need to define interfaces for the View?

 1. Since it's an interface you can change the view implementation. You can simple move your code from something that extends Activity to something that extends Fragment.
 2. Modularity: You can move the whole business logic, Presenter and View Interface in a standalone library project. Then you can use this library with the containing Presenter in various apps.
 3. You can easily write unit tests since you can mock views by implement the view interface. One could also introduce a java interface for the presenter to make unit testing by using mock presenter objects even more easy.
 4. Another very nice side effect of defining a interface for the view is that you don't get tempted to call methods of the activity / fragment directly from presenter. You get a clear separation because while implementing the presenter the only methods you see in your IDE's auto completion are those methods of the view interface. From our personal experiences we can say that this is very useful especially if you work in a team.

Please note that we could also use `MvpLceView<List<Country>>` instead of defining an (empty, because inherits methods) interface `CountriesView`. But having an dedicated interface `CountriesView` improves code readability and we are more flexible to define more View related methods in the future.

Next we define our views xml layout file with the required ids:

{% highlight xml %}
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

  <!-- Loading View -->
  <ProgressBar
    android:id="@+id/loadingView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:indeterminate="true"
    />

  <!-- Content View -->
  <android.support.v4.widget.SwipeRefreshLayout
    android:id="@+id/contentView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

    </android.support.v4.widget.SwipeRefreshLayout>


    <!-- Error view -->
    <TextView
      android:id="@+id/errorView"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      />

</FrameLayout>
{% endhighlight %}

The `CountriesPresenter` controls `CountriesView` and starts the `CountriesAsyncLoader`:

{% highlight java %}
public class CountriesPresenter extends MvpBasePresenter<CountriesView> {

  @Override
  public void loadCountries(final boolean pullToRefresh) {

    getView().showLoading(pullToRefresh);


    CountriesAsyncLoader countriesLoader = new CountriesAsyncLoader(
        new CountriesAsyncLoader.CountriesLoaderListener() {

          @Override public void onSuccess(List<Country> countries) {

            if (isViewAttached()) {
              getView().setData(countries);
              getView().showContent();
            }
          }

          @Override public void onError(Exception e) {

            if (isViewAttached()) {
              getView().showError(e, pullToRefresh);
            }
          }
        });

    countriesLoader.execute();
  }
}
{% endhighlight %}

The `CountriesFragment` which implements `CountriesView` looks like this:
{% highlight java %}
public class CountriesFragment
    extends MvpLceFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;
  CountriesAdapter adapter;

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

Not that much code to write, right? It's because the base class `MvpLceFragment` already implements the switching from loading view to content view or error view  for us. At first glance the list of generics parameter of `MvpLceFragment` may discourage you. Let me explain that: The first generics parameter is the type of the content view. The second is the Model that is displayed with this fragment. The third one is the View interface and the last one is the type of the Presenter. To summarize: `MvpLceFragment<AndroidView, Model, View, Presenter>`

Another thing you may have noticed is `getLayoutRes()`, which is a shorthand introduced in `MosbyFragment` for inflating a xml view layout:
{% highlight java %}
@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    return inflater.inflate(getLayoutRes(), container, false);
}
{% endhighlight %}

So instead of overriding `onCreateView()` you can override `getLayoutRes()`.

## ViewGroup
If you want to avoid Fragments in general you can do that. Mosby offers the same MVP scaffold as for Activities and Fragments for ViewGroups. The API is the same as for Activity and Fragment. Some default implementation are already available like `MvpFrameLayout`, `MvpLinearLayout` and `MvpRelativeLayout`.

## Delegation
You may be wondering how Mosby can provide the same API for all kind of Views (Activity, Fragment and ViewGroup) without having code clones (copy & paste the same code). The answer is delegation.
The methods of the delegates has been named to match the Activities or Fragments lifecycle method names (inspired by the latest AppCompatDelegate from appcompat support library) for better understanding  which delegate method should be called from which Activity or Fragment lifecycle method:

  - `MvpDelegateCallback`: Is an interface every `MvpView` in Mosby has to implement. Basically it just provides some MVP related methods like `createPresenter()` etc. This methods are internally called by `ActivityMvpDelegate` or `FragmentMvpDelegate`.
  - `ActivityMvpDelegate`: It's an interface. Usually you use `ActivityMvpDelegateImpl` which is the default implementation. All you have to do to bring Mosby MVP support to your custom Activity is to call the corresponding delegate method from Activities lifecycle method like `onCreate()`, `onPause()`, `onDestroy()` etc. and to implement `MvpDelegateCallback`:

  {% highlight java %}
  public abstract class MyActivity extends Activity implements MvpDelegateCallback<> {

    protected ActivityMvpDelegate mvpDelegate = new ActivityMvpDelegateImpl(this);

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

  - `FragmentMvpDelegate`: Same as `ActivityMvpDelegate` but for `Fragments`. To bring Mosby MVP support to your custom Fragment all you have to do is the same as shown above for Activities: Create a `FragmentMvpDelegate` and call the methods from the corresponding Fragment lifecycle method. Your Fragment has to implement `MvpDelegateCallback` as well. Usually you use the default delegate implementation `FragmentMvpDelegateImpl`
  - `ViewGroupMvpDelegate`: This delegate is used for `ViewGroup` like `FrameLayout` etc. to bring Mosby MVP support to your custom `ViewGroup`. The lifecycle methods are simpler compared to Fragments ones: `onAttachedToWindow()` and `onDetachedFromWindow()`. The default implementation is `ViewGroupMvpDelegateImpl`.

Another advantage of delegation is that you can integrate Mosby in any other third party library or framework. Just implement `MvpDelegateCallback` and instantiate a delegate and call the corresponding delegate methods along lifecycle events.

## Presentation Model
In a perfect world we would get the data (Model) exactly the way optimized for displaying it in our GUI (View). Quite often we retrieve data from a backend over a public API that can't be changed to fit the needs of your UI. In fact, it's not a good idea that the backend provides an API depending on your UI because if you change your UI you may also have to change the backend. Therefore you have to transform the model into something your GUI can display easily. A typically example is to load a list of Items, let's say a List of Users, from a REST json API and display them in a `ListView`. In a perfect world with MVP this works like this:

![Presentation Model - Simple Adapter]({{ site.baseurl }}/images/p-model1.png)

Nothing new here. `List<User>` gets loaded and the GUI displays the Users in a `ListView` by using  `UserAdapter`. I'm pretty sure that you have used `ListView` and `Adapter` million times before, but have you ever thought about the idea behind `Adapter`? Adapter makes your model displayable through android UI widgets. This is the [adapter design pattern](https://sourcemaking.com/design_patterns/adapter), hence the name. What if we want to support phones and tablets but both display items in a different way? We implement two adapters: `PhoneUserAdapter` and `TabletUserAdapter` and we pick the right one at runtime.

That is the "perfect world" scenario. What if we have to sort the user list or display some things that has to be calculated in a more complex (and CPU intensive) way? We can't do that in  `UserAdapter` because you will notice scroll performance issues because the hard work is done on the  main UI thread. Therefore, we do that in a separated thread. There are two questions: The first is how do we transform the data? Do we take our User class and add some additional fields to User class? Do we override values of the User class?

{% highlight java %}
public class User {
  String firstname;
  String lastname;
}
{% endhighlight %}

Let's assume that our `UserView` wants to display the full name and calculates a ranking the list get sorted accordingly:

{% highlight java %}
public class User {
  String firstname;
  String lastname;
  int ranking;

  public String getFullname(){
    return firstname +" "+lastname;
  }
}
{% endhighlight %}

While introducing a method `getFullname()` is ok adding a field `ranking` may cause problems, because we assume that `User` is retrieved from backend and has no `ranking` in it's json representation. So first it may cause confusion if you look at your json api feed and compare that to our `User` class and last but not least ranking will be zero as default value because we haven't computed the ranking yet. If we would have used an object instead of an integer then the default value would be _null_ and it's likely that we run into NullPointerException.

The solution is to introduce a [Presentation Model](http://martinfowler.com/eaaDev/PresentationModel.html). This model is just a class optimized for our GUI:

{% highlight java %}
public class UserPresentationModel {
  String fullname;
  int ranking;

  public UserPresentationModel(String fullname, int ranking) { ... }
}
{% endhighlight %}

By doing so we are sure that `ranking` is always set to a concrete value and that fullname doesn't get computed while scrolling the ListView (PresentationModel gets instantiated in separated thread). UserView now displays `List<UserPresentationModel>` instead of `List<User>`.

The second question is: Where to do that async transformation? View, Model or Presenter?
It would be obvious that the View does that transformation because the View knows best how things get displayed on screen.


![Presentation Model - View transform]({{ site.baseurl }}/images/p-model2.png)


`PresentationModelTransformer` is the component that takes a `List<User>` and "transforms" that to `List<UserPresentationModel>` (adapter pattern, so we have two adapters: one to convert to presentation model and UserAdapter to display them in ListView). The advantage on integrating `PresentationModelTransformer` in the view is that the view knows how to display things and could easily internally change between phone and tablet optimized presentation models (maybe the tablet UI has other requirements then the phone UI). However, the big disadvantage is that now the view has to control the async thread and the view state (displaying ProgressBar while transformation is in progress?!?) which clearly is the job of the Presenter. Therefore, **it's not a good idea to let the transformation be part of the View**. Including the transformation in the Presenter is the way to go:

![Presentation Model - Presenter transformer]({{ site.baseurl }}/images/p-model3.png)

As we have already discussed before, the `Presenter` is responsible to coordinate the View and therefore the Presenter tells the view to display the ListView after `UserPresentationModel` transformation is finished. Also the presenter has control of all async threads (transformation runs async) and cancels them if needed. By the way: With RxJava you can use operations like `map()` or `flatMap()` to do this transformation painless (regarding threading). If we want to support phones and tablets we could define two Presenters `PhoneUserPresenter` and `TabletUserPresenter` with different `PresentationModelTransformer` implementations. In Mosby the View creates the Presenter. Since the View knows at runtime if the View is a phone or tablet it can choose at runtime Presenter to instantiate (`PhoneUserPresenter` or `TabletUserPresenter`). Alternatively you could use a single `UserPresenter` for phone and tablet and just replace `PresentationModelTransformer` implementation i.e. by using dependency injection.
