---
layout: page
title: Getting Started
permalink: /getting-started/
---

# Getting started
Here are some simple examples how to use Mosby to build an MVP based architecture. We assume that you already know how "MVP - passive view", if not you should consider to read the [MVP section](http://hannesdorfmann.com/mosby/mvp/) to understand the basics of MVP before continue.

## Hello MVP World
In this very simple example we assume that the view displays a `TextView` displaying either "Hello " and a random number or "Goodbye" and a random number. If "Hello" is displayed the text color will be red, otherwise "Goodbye" will be displayed with blue text color. Generating the greeting text is done in an `AsyncTask` and we assume that generating the greeting text (random number computed and concatenated at the end of the greeting text) is super cpu expensive and take 2 seconds.

{% highlight java %}
// "Business logic" component
class GreetingGeneratorTask extends AsyncTask<Void, Void, Integer> {

  // Callback - Listener
  interface GreetingTaskListener{
    public void onGreetingGenerated(String greetingText);
  }

  private String baseText;
  private GreetingTaskListener listener;

  public GreetingGeneratorTask(String baseText, GreetingTaskListener listener){
    this.baseText = baseText;
    this.listener = listener;
  }

  // Simulates computing and returns a random integer
  @Override
  protected Integer doInBackground(Void... params) {
    try {
      Thread.sleep(2000); // Simulate computing
    } catch (InterruptedException e) { }

    return (int) (Math.random() * 100);
  }

  @Override
  protected void onPostExecute(Integer randomInt){
    listener.onGreetingGenerated(baseText + " "+randomInt);
  }
}
{% endhighlight %}

{% highlight java %}
// View interface
interface HelloWorldView extends MvpView {

  // displays "Hello" greeting text in red text color
  void showHello(String greetingText);

  // displays "Goodbye" greeting text in blue text color
  void showGoodbye(String greetingText);
}
{% endhighlight %}

{% highlight java %}
// The presenter that coordinates HelloWorldView and business logic (GreetingGeneratorTask)
class HelloWorldPresenter extends MvpBasePresenter<HelloWorldView> {

  // Greeting Task is "business logic"
  private GreetingGeneratorTask greetingTask;

  private void cancelGreetingTaskIfRunning(){
    if (greetingTask != null){
      greetingTask.cancel(true);
    }
  }

  public void greetHello(){
    cancelGreetingTaskIfRunning();

    greetingTask = new GreetingGeneratorTask("Hello", new GreetingTaskListener(){
      public void onGreetingGenerated(String greetingText){
        if (isViewAttached())
          getView.showHello(greetingText);
      }
    });
    greetingTask.execute();
  }

  public void greetGoodbye(){
    cancelGreetingTaskIfRunning();

    greetingTask = new GreetingGeneratorTask("Goodbye", new GreetingTaskListener(){
      public void onGreetingGenerated(String greetingText){
        if (isViewAttached())
          getView.showGoodbye(greetingText);
      }
    });
    greetingTask.execute();
  }

  // Called when Activity gets destroyed, so cancel running background task
  public void detachView(boolean retainPresenterInstance){
    super.detachView(retainPresenterInstance);
    cancelGreetingTaskIfRunning();
  }
}
{% endhighlight %}

{% highlight java %}
public class HelloWorldActivity extends MvpActivity<HelloWorldView, HelloWorldPresenter>
                                implements HelloWorldView {

  @Bind(R.id.greetingTextView) TextView greetingTextView;

  @Override
  protected void onCreate(Bundle savedState){
    super.onCreate(savedState);
    setContentView(R.layout.activity_helloworld);
    Butterknife.bind(this);
  }

  @Override // Called internally by Mosby
  public HelloWorldPresenter createPresenter(){
    return new HelloWorldPresenter();
  }

  @OnClick(R.id.helloButton)
  public void onHelloButtonClicked(){
    presenter.greetHello();
  }

  @OnClick(R.id.goodbyeButtonClicked)
  public void onGoodbyeButtonClicked(){
    presenter.greetGoodbye();
  }

  @Override
  public void showHello(String greetingText){
    greetingTextView.setTextColor(Color.RED);
    greetingTextView.setText(greetingText);
  }

  @Override
  public void showGoodbye(String greetingText){
    greetingTextView.setTextColor(Color.BLUE);
    greetingTextView.setText(greetingText);
  }
}
{% endhighlight %}

As you can see `HelloWorldActivity` now contains only code related to UI components. No business logic, no big Activity with 1000+ lines of spaghetti code. With this clear separation of concerns between View (HelloWorldActivity), Presenter (HelloWorldPresenter) and business logic (GreetingGeneratorTask) you are able to write maintainable, loosely coupled and testable code. **That's what MVP is all about.**

## LCE Example
The following example is a little bit more complex and closer to a real world application. Assume we want to display a list of countries in a `RecyclerView`. The list of countries are loaded from a web service (async) and takes a few seconds. We will use [Retrofit](http://square.github.io/retrofit/) as http networking library. While loading we want to display a `ProgressBar`. If an error occurs we want to display an error message. Furthermore, we want to use a `SwipeRefreshLayout` so that the user refresh the list of countries. We call this kind of View **LCE-View** (Loading-Content-Error) since the View has three states: displaying loading, displaying content or displaying an error view. Mosby provides a template for such Views: `MvpLceActivity` or `MvpLceFragment`. Those template base classes already handle changing the views state (with a fade animation).

{% highlight java %}
// Business logic: We use Retrofit to load a list of Countries from a web service
interface CountriesApi {

  @GET("/countries")
  void getCountries(Callback<List<Country>> callback);
}
{% endhighlight %}

{% highlight java %}
// View interface
interface CountriesView extends MvpLceView<List<Country>> {
  // MvpLceView already defines LCE methods:
  //
  // void showLoading(boolean pullToRefresh)
  // void showError(Throwable t, boolean pullToRefresh)
  // void setData(List<Country> data)
  // void showContent()
}
{% endhighlight %}

{% highlight java %}
class CountriesPresenter extends MvpBasePresenter<CountriesView> {

  private CountriesApi api;

  public void loadCountries(final boolean pullToRefresh){

    api.getCountries(new Callback<List<Country>>(){
      @Override
      public void success(List<Country> countries, Response response) {
        if (isViewAttached()){
          getView().setData(countries);
          getView().showContent();
        }
      }

      @Override
      public void failure(RetrofitError retrofitError) {
        if(isViewAttached())
          getView().showError(retrofitError.getCause(), pullToRefresh);
      }
    });
  }
}
{% endhighlight %}

{% highlight java %}
public class CountriesFragment
    extends MvpLceFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  CountriesAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_countries, container, false);
  }

  @Override public void onViewCreated(View view, Bundle savedInstance) {
    super.onViewCreated(view, savedInstance);
    ButterKnife.bind(this, view);
    // contentView is SwipeRefreshLayout
    contentView.setOnRefreshListener(this);

    // Setup recycler view
    adapter = new CountriesAdapter(getActivity());
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);
    loadData(false);
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public CountriesPresenter createPresenter() {
    return new CountriesPresenter();
  }

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    contentView.setRefreshing(pullToRefresh);
  }
}
{% endhighlight %}

## LCE ViewState example
What happens in the previous LCE example if the user rotates the screen? A new Fragment gets created that an the UI starts again with showing the `ProgressBar` even if before the screen orientation change we already were displaying data (list of countries). Mosby offers a feature called **ViewState** to handle screen orientation changes. Check the [ViewState section] for details and how it works. Here in this "Getting Started" section we just want to show how to add ViewState support to the previous LCE example so that your app will still be in the state as before screen orientation changes i.e. showing list of countries in portrait and still displaying list of countries in landscape. All we have to do is to extend from `MvpLceViewStateFragment` instead of `MvpLceFragment` and implement `createViewState()`:
{% highlight java %}
public class CountriesFragment
    extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  CountriesAdapter adapter;

  @Override
  public void onCreate(Bundle savedState){
    super.onCreate(savedState);
    setRetainInstance(true); // Enable retaining presenter / viewstate
  }

  @Override
  public ViewState<CountriesView> createViewState(){
    return RetainingLceViewState();
  }

  ...
  // Everything else remains the same as shown in the previous LCE example

}
{% endhighlight %}

Please note that also Activities and ViewGroup support the ViewState feature.


## Kotlin Example
Kotlin is the new hope in android development. Starting with Mosby 2.0 Kotlin is supported. In this example we will show a very basic LCE example where we generate (async) a list of super heroes and display them in a RecyclerView.

{% highlight kotlin %}
// Business logic: AsyncTask generates list of heroes and invokes successful lambda or error lambda as "callback"
public class AsyncHeroesTask(val pullToRefresh: Boolean,
                             val successful: (List<Hero>, Boolean) -> Unit,
                             val error: (Exception, Boolean) -> Unit) : AsyncTask<Void, Void, List<Hero>>() {

    // Simple static counter since we simulate errors on every second request
    companion object Counter {
        var requestCounter: Int = 0
    }


    override fun doInBackground(vararg params: Void?): List<Hero>? {

        Thread.sleep(2000) // Simulate network delay

        requestCounter++

        // Simulate network error every second request --> returning null means error
        if (requestCounter % 2 != 0) return null;


        var heroes = arrayListOf(
                Hero("Batwoman", "https://upload.wikimedia.org/wikipedia/en/2/24/Batwoman.png"),
                Hero("Spider-Man","http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/1j-720x1091.jpg"),
                ...
        )

        Collections.shuffle(heroes)

        return heroes;
    }

    override fun onPostExecute(heroes: List<Hero>?) {
        when (heroes) {
            null -> error(Exception(), pullToRefresh)
            else -> successful(heroes, pullToRefresh)
        }
    }
}
{% endhighlight %}

{% highlight kotlin %}
interface HeroesView : MvpLceView<List<Hero>> {
  // LCE methods inherited
}
{% endhighlight %}

{% highlight kotlin %}
public class HeroesPresenter : MvpBasePresenter<HeroesView> () {

    private var loaderTask: AsyncHeroesTask ? = null

    fun loadHeroes(pullToRefresh: Boolean) {

        cancelIfRunning();

        // Show Loading
        view?.showLoading(pullToRefresh)

        // execute loading
        loaderTask = AsyncHeroesTask(
                pullToRefresh,
                { heroes, pullToRefresh ->  // successful lambda / callback
                    view?.setData(heroes) // no isViewAttached() check needed because kotlin offers null safety as language feature
                    view?.showContent()
                },
                { exception, pullToRefresh ->  // error lambda / callback
                    view?.showError(exception, pullToRefresh)
                }
        )

        loaderTask?.execute()

    }

    fun cancelIfRunning() {

        // Cancel any previous one
        loaderTask?.cancel(true);
    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)

        // Keep async tasks running if retaining, otherwise cancel
        if (!retainInstance) {
            cancelIfRunning()
        }
    }

}
{% endhighlight %}

{% highlight kotlin %}
public class HeroesActivity : HeroesView, MvpLceViewStateActivity<SwipeRefreshLayout, List<Hero>, HeroesView, HeroesPresenter>(), SwipeRefreshLayout.OnRefreshListener {

    var adapter: HeroesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<MvpLceViewStateActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heroes)
        setRetainInstance(true) // Enable retaining presenter / viewstate
        contentView.setOnRefreshListener(this)

        val recyclerView = findViewById(R.id.recyclerView) as RecyclerView

        adapter = HeroesAdapter(this, LayoutInflater.from(this))
        recyclerView.setAdapter(adapter)
        recyclerView.setLayoutManager(GridLayoutManager(this, 2))
    }

    override fun createPresenter(): HeroesPresenter {
        return HeroesPresenter()
    }

    override fun createViewState(): LceViewState<List<Hero>, HeroesView> {
        return RetainingLceViewState()
    }

    override fun setData(data: List<Hero>?) {
        adapter?.items = data
        adapter?.notifyDataSetChanged()
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadHeroes(pullToRefresh)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun showContent() {
        super<MvpLceViewStateActivity>.showContent()
        contentView.setRefreshing(false)
    }

    override fun showError(t: Throwable, pullToRefresh: Boolean) {
        super<MvpLceViewStateActivity>.showError(t, pullToRefresh)
        contentView.setRefreshing(false)
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super<MvpLceViewStateActivity>.showLoading(pullToRefresh)
        contentView.setRefreshing(pullToRefresh)
    }
}
{% endhighlight %}
