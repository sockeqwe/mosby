---
layout: page
title: Writing an App
permalink: /first-app/
---

# Writing an App
_In [Model-View-Presenter fundamentals]({{ site.baseurl }}/mvp/) we have discussed what MVP is and how you can use Mosby to implement an MVP based architecture. Furthermore, we have explained how [Mosby's ViewState]({{ site.baseurl }}/viewstate/) feature work. Now let's put that all together and lets write an App powered by Mosby_

Usually such _"Getting started"_ sections just scratch on the surface with very simple examples. We think that sometimes such simple examples are to simple to demonstrate the power of a library. Therefore, we have decided to implement a complex app coming close to a real world app: a mail client. If you are looking for a very simple example then you should check out the [simple basics sample on Github](https://github.com/sockeqwe/mosby/tree/master/sample). The source code should be self explaining.

As already said on this page we want to show how to write a complex app powered by Mosby. We will write an app that mimics a mail client. The functional requirements are as follows:

 - The app is not connected to a real POP3 or IMAP server. All the data is randomly generated in memory on app start. There is no persistent layer like local sqlite database.
 - A `Mail` is linked to a `Person` as sender and another Person as receiver.
 - Every mail is associated to exactly one `Label`. A Label is just something like a "folder". A mail can be assigned to one Label. One Label has arbitrary many mails assigned.
 - A Label is basically just a String. In this sample app there are four labels: "Inbox", "Sent", "Spam" and "Trash". So deleting a mail form inbox is just reassigning the label of the mail from "Inbox" to "Trash".
 - The app should be able to display a list of mails for a given label.
 - A User has to be authenticated to use the app. If the user is not authenticated no data should be displayed. Furthermore, the user should be forced to login.
 - The user of the app should be able to send a mail.
 - The app provides a search mechanism to search for keywords. This should be implemented by using pagination (load more search results as the user has scrolled to the end of the list).
 - By clicking on a Peson (i.e. the sender of a mail) a "profile overview" should be displayed with a list of all mails received from the given person and additional information about the person (curriculum vitae).
 - The view's state should be retained across screen orientation changes.

The final app looks like this:

<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/_dEYtXgoyBM?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
</p>


The APK file can be downloaded from [here](https://github.com/sockeqwe/mosby/releases/download/1.2.0/sample-mail-debug.apk).
Now let's step together through the source code (can be found on [Github](https://github.com/sockeqwe/mosby/tree/master/sample-mail/)) and discuss some implementation details. Of course the whole app is based on Mosby and as you have seen in the video above everything keeps it's state during orientation changes. So we use Mosby's `ViewState` feature as well.

## Business logic
`MailProvider` is the central business logic element where we can query list of mails, send mails and so on. Furthermore, an `AccountManager` exists who is responsible to authenticate a user. Internally for the business logic (`MailProvider` and `AccountManager`) we use RxJava. Usually you don't have to take a look inside the business logic implementation details. If you are not familiar with RxJava, don't worry. All you have to know is that RxJava offers an `Observable` to do some kind of query and as callback the methods `onData()`, `onCompleted()` or `onError()` gets called. If you are familiar with RxJava, please DON'T look at the code! It's pretty ugly as we changed mind several times during development and didn't refactored everything as we would do for a real app. Please note that this sample app is about Mosby and not how to write a clean business logic with RxJava. Note also that even if we have used Dagger2 for dependency injection this is not a reference app for Dagger2 nor for material design.

As you see in the video we are simulating network traffic by adding a delay of two seconds to every request (like loading a list of mails). We also simulate network problems: Every fifth request will fail (that's why you see the error view quite often in the video). Furthermore, we simulate authentication problems: After 15 requests we say that the user is not authenticated anymore (i.e. in a real world an access token or session has been expired). Thus the user has to sign in again (a login button will be dipslayed).

## Sign in
`LoginActivity` is the activity we launch to show the login form. `LoginActivity` is basically just a container for a retaining `LoginFragment`. Mosby ships with `MvpLceViewStateFragment implements MvpLceView` which displays either the content (like a RecyclcerView to display a list of mails) or a  loading indicator (like a ProgressBar) or an error view (like a TextView displaying an error message). This might be handy, because you don't have to implement the switching of displaying content, displaying error, displaying loading by your own, but you shouldn't use it as base class for everything. Only use it if your view can reach all three view states (showing loading, content and error). Have a look at the sign in view:

{% highlight java %}
public interface LoginView extends MvpView {

  // Shows the login form
  public void showLoginForm();

  // Called if username / password is incorrect
  public void showError();

  // Shows a loading animation while checking auth credentials
  public void showLoading();

  // Called if sign in was successful. Finishes the activity. User is authenticated afterwards.
  public void loginSuccessful();
}
{% endhighlight %}

At first glance you might assume that `LoginView` is a `MvpLceView` with just having `showContent()` renamed to `showLoginForm()`. Have a closer look at `MvpLceView` definition:

{% highlight java %}
public interface MvpLceView<M> extends MvpView {

  public void showLoading(boolean pullToRefresh);

  public void showContent();

  public void showError(Throwable e, boolean pullToRefresh);

  public void setData(M data);

  public void loadData(boolean pullToRefresh);
}
{% endhighlight %}

Methods like `loadData()` and `setData()` are not needed in `LoginView` nor make it sense to have pull-to-refresh support. You could simply use MvpLceView anyway and implement that methods with an empty implementation. But that is a bad idea, because defining an interface is like making a contract with other software components: Your interface promises that the method is "callable" (in the sense of does something useful), but doing nothing by invoking this method is a violation of that contract. Also your code gets harder to understand and maintain if you have plenty of methods that get called from some other components but are doing nothing. Furthermore, if you use `MvpLceView` usually you would use `MvpLcePresenter` as base class for `LoginPresenter`. The problem is that `MvpLcePresenter` is "optimized" for `MvpLceView`. So you may have to implement some workarounds in your Presenter which extends from MvpLcePresenter to achieve what you want to do. Simply avoid that kind of problems by not using LCE related classes if your view doesn't have full LCE support.

Hence we don't use LCE related for the login and write our own `LoginView`(already shown above), `LoginPresenter` and `LoginViewState`. The `LoginPresenter` is nothing special. All it does is it takes the username and password from `LoginView` and passes it to `AccountManager`. If login was successful an `LoginSuccessfulEvent` will be posted along an EventBus. We use an EventBus to communicate such events amongst each Presenter (will be explained later).

_Please note that we use an EventBus in this sample app since we believe that the most readers are familiar with the concept of EventBus. You could replace EventBus with any other push alike mechanism like RxJava Observables etc._

{% highlight java %}
public class LoginPresenter extends MvpBasePresenter<LoginView> {

  private AccountManager accountManager;
  private EventBus eventBus;

  @Inject public LoginPresenter(AccountManager accountManager,
      EventBus eventBus) {
    this.accountManager = accountManager;
    this.eventBus = eventBus;
  }

  public void doLogin(AuthCredentials credentials) {

    if (isViewAttached()) {
      getView().showLoading();
    }

    // Kind of "callback"
    subscriber = new Subscriber<Account>() {
      @Override public void onCompleted() {
        if(isViewAttached()){
          getView().loginSuccessful();
        }
      }

      @Override public void onError(Throwable e) {
        if (isViewAttached()){
          getView().showError();
        }
      }

      @Override public void onNext(Account account) {
        eventBus.post(new LoginSuccessfulEvent(account));
      }
    };

    // do the login
    accountManager.doLogin(credentials).subscribe(subscriber);
  }
}
{% endhighlight %}

The `LoginFragment` is the View controlled by `LoginPresenter`.
{% highlight java %}
 public class LoginFragment extends MvpViewStateFragment<LoginView, LoginPresenter>
    implements LoginView {

  @InjectView(R.id.username) EditText username;
  @InjectView(R.id.password) EditText password;
  @InjectView(R.id.loginButton) ActionProcessButton loginButton;
  @InjectView(R.id.errorView) TextView errorView;
  @InjectView(R.id.loginForm) ViewGroup loginForm;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_login;
  }

  @Override public ViewState createViewState() {
    return new LoginViewState();
  }

  @Override public LoginPresenter createPresenter() {
    return new LoginPresenter();
  }

  @OnClick(R.id.loginButton) public void onLoginClicked() {

    // Check for empty fields
    String uname = username.getText().toString();
    String pass = password.getText().toString();

    loginForm.clearAnimation();

    // Start login
    presenter.doLogin(new AuthCredentials(uname, pass));
  }

  // Called first time the fragment starts
  @Override public void onNewViewStateInstance() {
    showLoginForm();
  }

  @Override public void showLoginForm() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoginForm();

    errorView.setVisibility(View.GONE);
    setFormEnabled(true);
    loginButton.setLoading(false);
  }

  @Override public void showError() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowError();

    loginButton.setLoading(false);

    if (!isRestoringViewState()) {
      // Enable animations only if not restoring view state
      loginForm.clearAnimation();
      Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
      loginForm.startAnimation(shake);
    }

    errorView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {

    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoading();
    errorView.setVisibility(View.GONE);
    setFormEnabled(false);

    loginButton.setLoading(true);
  }

  private void setFormEnabled(boolean enabled) {
    username.setEnabled(enabled);
    password.setEnabled(enabled);
    loginButton.setEnabled(enabled);
  }

  // Called when login was successful
  @Override public void loginSuccessful() {
    getActivity().finish();
  }

}
{% endhighlight %}

As you can see `LoginFragment` now contains only code related to UI components. No business logic, no big Fragment with 1000+ lines of spaghetti code. With this clear separation of concerns between View (LoginFragment), Presenter (LoginPresenter) and business logic (AccountManager) you are able to write maintainable, loosly coupled and testable code. **That's what MVP is all about.**

Last but not least we also want to handle screen orientation changes (rotating device from portrait to landscape and vice versa). Mosby provides a mechanism for that called [ViewState]({{ site.baseurl }}/viewstate/). ViewState implementations for `MvpLceView` are already provided by Mosby. However `LoginView` is not a `MvpLceView` (discussed above) and therefore needs its own ViewState implementation. Writing a custom ViewState is easy:

{% highlight java %}
public class LoginViewState implements ViewState<LoginView> {

  final int STATE_SHOW_LOGIN_FORM = 0;
  final int STATE_SHOW_LOADING = 1;
  final int STATE_SHOW_ERROR = 2;

  int state = STATE_SHOW_LOGIN_FORM;

  public void setShowLoginForm() {
    state = STATE_SHOW_LOGIN_FORM;
  }

  public void setShowError() {
    state = STATE_SHOW_ERROR;
  }

  public void setShowLoading() {
    state = STATE_SHOW_LOADING;
  }

  /**
   * Is called from Mosby to apply the view state to the view.
   * We do that by calling the methods from the View interface (like the presenter does)
   */
  @Override public void apply(LoginView view, boolean retained) {

    switch (state) {
      case STATE_SHOW_LOADING:
        view.showLoading();
        break;

      case STATE_SHOW_ERROR:
        view.showError();
        break;

      case STATE_SHOW_LOGIN_FORM:
        view.showLoginForm();
        break;
    }
  }

}
{% endhighlight %}

We simply store the current view state internally as an integer `state`. The method `apply()` gets called from Mosby internally and this is the point where we restore the view's state  by calling the same methods defined in `LoginView` interface as the Presenter does.
You may wonder how the ViewState is connected to your Fragment or Activity. `MvpViewStateFragment` has a method `createViewState()` called by Mosby internally which you have to implement. You just have to return a `LoginViewState` instance. However, you have to set the LoginViewState's' internal state by hand. Typically you do that in the methods defined by `LoginView` interface as shown below:
{% highlight java %}

public class LoginFragment extends MvpViewStateFragment<LoginView, LoginPresenter>
    implements LoginView {

  @Override public void showLoginForm() {

    // Set View state
    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoginForm();

    errorView.setVisibility(View.GONE);

    ...
  }

  @Override public void showError() {

    // Set the view state
    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowError();

    if (!isRestoringViewState()) {
      // Enable animations only if not restoring view state
      loginForm.clearAnimation();
      Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
      loginForm.startAnimation(shake);
    }

    errorView.setVisibility(View.VISIBLE);

    ...
  }

  @Override public void showLoading() {

    // Set the view state
    LoginViewState vs = (LoginViewState) viewState;
    vs.setShowLoading();

    errorView.setVisibility(View.GONE);

    ...
  }
}
{% endhighlight %}

Sometimes you have to know if the method gets called from Presenter or because of restoring ViewState, typically when working with animations. You can check that with `isRestoringViewState()` like `showError()` does (see above).

Since `LoginView` and `LoginViewState` are plain old java classes with no dependencies to the android framework you can write plain old java unit test for `LoginViewState`:

{% highlight java %}
@Test
public void testShowLoginForm(){

  final AtomicBoolean loginCalled = new AtomicBoolean(false);
  LoginView view = new LoginView() {

    @Override public void showLoginForm() {
      loginCalled.set(true);
    }

    @Override public void showError() {
      Assert.fail("showError() instead of showLoginForm()");
    }

    @Override public void showLoading() {
      Assert.fail("showLoading() instead of showLoginForm()");
    }

    @Override public void loginSuccessful() {
      Assert.fail("loginSuccessful() instead of showLoginForm()");
    }
  };

  LoginViewState viewState = new LoginViewState();
  viewState.setShowLoginForm();
  viewState.apply(view, false);
  Assert.assertTrue(loginCalled.get());

}
{% endhighlight %}

To test if `LoginFragment implements LoginView` restores it's state correctly during screen orientation changes it's enough to test the `LoginViewState` because you can assume that Mosby's internals during screen orientation on your a real device are working correctly.

So far we only have tested if restoring works correctly. We also have to test if we are setting the view state correctly. However, that is done in `LoginFragment`, so we have to test the Fragment rather than the `LoginViewState`. Thanks to [Robolectric](http://robolectric.org/) this is also straightforward:
{% highlight java %}
@Test
public void testSetingState(){
  LoginFragment fragment = new LoginFragment();
  FragmentTestUtil.startFragment(fragment);
  fragment.showLoginForm();

  LoginViewState vs = (LoginViewState) fragment.getViewState();

  Assert.assertEquals(vs.state, vs.STATE_SHOW_LOGIN_FORM);
}
{% endhighlight %}

We simply call `showLoginForm()` and we check if the ViewState is `STATE_SHOW_LOGIN_FORM` after this call.

You could also test if `LoginFragment` handles screen orientation changes correctly by writing instrumentation test (i.e. with espresso), but the point is that in Mosby you have this clear separation between view and view's state. Hence, you can test the view state independently.

## Base classes
If you have a closer look at the video you will see that the most of the views can switch between showing a login button and the usual Loading-Content-Error (LCE) things. Hence we define a Base view interface:

{% highlight java %}
// @param <M> The type of the model displayed by this view
public interface AuthView<M> extends MvpLceView<M> {
  public void showAuthenticationRequired();
}
{% endhighlight %}

`AuthView` extends  `MvpLceView`. The method `showAuthenticationRequired` forces the view to show the login button. In the mail app we use retaining Fragments and Activities just as container for Fragments. Hence we can define a base Fragment that we can use to derive from to avoid to implement the `showAuthenticationRequired()` again and again.

{% highlight java %}
public abstract class AuthFragment<AV extends View, M, V extends AuthView<M>, P extends MvpPresenter<V>>
    extends MvpLceViewStateFragment<AV, M, V, P> implements AuthView<M> {

  protected View authView;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    authView = view.findViewById(R.id.authView);
    authView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onAuthViewClicked(); // Starts the intent to show login form
      }
    });
  }

  @Override public void showAuthenticationRequired() {
    AuthViewState vs = (AuthViewState) viewState;
    vs.setShowingAuthenticationRequired();

    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    contentView.setVisibility(View.GONE);
    authView.setVisibility(View.VISIBLE);
  }
}
{% endhighlight %}

To control a `AuthView` like an `AuthFragment` we need a Presenter. Let's define `BaseRxAuthPresenter`:

{% highlight java %}
public class BaseRxAuthPresenter<V extends AuthView<M>, M> extends MvpLceRxPresenter<V, M> {

  protected EventBus eventBus;
  protected MailProvider mailProvider;

  public BaseRxAuthPresenter(MailProvider mailProvider, EventBus eventBus) {
    this.eventBus = eventBus;
    this.mailProvider = mailProvider;
  }

  @Override public void attachView(V view) {
    super.attachView(view);
    eventBus.register(this);
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    eventBus.unregister(this);
  }

  // Called when an error has occurred while quering data from mailProvider
  @Override protected void onError(Throwable e, boolean pullToRefresh) {
    if (e instanceof NotAuthenticatedException) {
      eventBus.post(new NotAuthenticatedEvent());
    } else {
      super.onError(e, pullToRefresh);
    }
  }

  public void onEventMainThread(NotAuthenticatedEvent event) {
    if (isViewAttached()) {
      getView().showAuthenticationRequired();
    }
  }

  public void onEventMainThread(LoginSuccessfulEvent event) {
    if (isViewAttached()) {
      getView().loadData(false);
    }
  }
}
{% endhighlight %}

The idea is to use an `EventBus` to propagate that the user is not authenticated (`NotAuthenticatedException` will be thrown by `MailProvider`). To do so, we post `NotAuthenticatedEvent` to the EventBus to inform all subscribed Presenters that the login button should be displayed. You can see this behavior even better if we open the app on a tablet:

<p>
 <iframe width="640" height="480" src="https://www.youtube.com/embed/d898QONavBo?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
 </p>

The main menu as well as inbox fragment displays the login button. We do pretty the same when the user has been signed in successfully: A `LoginSuccessfulEvent` will be propagated over the EventBus and the Presenter will start loading the data instead of displaying the login button:

 ![Model-View-Presenter]({{ site.baseurl }}/images/login-eventbus.jpg)

Since we are also going to display list of items (like a list of mails) through a `RecyclcerView`. We also implement pull-to-refresh support (by using `SwipeRefreshLayout`) and a view that is dipslayed when the list of items is empty.

{% highlight java %}
public abstract class AuthRefreshRecyclerFragment<M extends List<? extends Parcelable>, V extends AuthView<M>, P extends MvpPresenter<V>>
    extends AuthRefreshFragment<M, V, P> {

  protected View emptyView;
  protected RecyclerView recyclerView;
  protected Adapter<M> adapter;

  protected abstract Adapter<M> createAdapter();

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    emptyView = view.findViewById(R.id.emptyView);
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    adapter = createAdapter();
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);
  }

  @Override public void setData(M data) {
    adapter.setItems(data);
    adapter.notifyDataSetChanged();
  }

  @Override public AuthViewState<M, V> createViewState() {
    return new AuthCastedArrayListViewState<>();
  }

  @Override public M getData() {
    return adapter.getItems();
  }

  @Override public void showContent() {
    if (adapter.getItemCount() == 0) {
        emptyView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.GONE);
    }

    super.showContent();
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    if (!pullToRefresh) {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    if (!pullToRefresh) {
      emptyView.setVisibility(View.GONE);
    }
  }
}
{% endhighlight %}

So at the end `AuthRefreshRecyclerFragment` has already implemented "showing login button", "pull-to-refresh", "empty view" (if list of items is empty), "ViewState" along with the other LCE features (inherited from `MvpLceViewStateFragment`) like showing "loading view", "content view", and "errorView". So a fragment that wants to display items in a `RecyclerView` can extends from `AuthRefreshRecyclerFragment` and only has to provide a custom `RecyclerView.Adapter`. To set the Adapter for a fragment the subclass has to implement `createAdapter()` and the rest should work out of the box.

## List of Mails
Displaying a list of Mails is not that impressive. It's just `MailsFragment extends AuthRefreshRecyclerFragment` controlled by `MailsPresenter` that loads a `List<Mail>` from `MailProvider`. But that's a very good time to demonstrate that by having a clear separation of `View` and `Presenter` some complex things can be implemented easyily. In the sample app you can star and unstar mails. Have a look at the list of mails shown on a tablet.

<p>
 <iframe width="640" height="480" src="https://www.youtube.com/embed/7HPtTg35ZS0?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
</p>

As you have seen in the video above the Tablet UI display a list of mails on the left and a details view of the selected mail on the right. If we star a mail in details view the view on the left displaying a list of mails marks the same mail as stared as well. At first glance you may think that this is too complex. It seems that you have to respect so many things for implementing that. Don't worry, with a clean architecture like MVP and an EventBus it's quite easy. `MailsPresenter` extends from `BaseRxMailPresenter` that implements listening for `MailStaredEvent` and `MailUnstaredEvent`:

 {% highlight java %}
 public class BaseRxMailPresenter<V extends BaseMailView<M>, M>
     extends BaseRxAuthPresenter<V, M> {

   @Inject public BaseRxMailPresenter(MailProvider mailProvider, EventBus eventBus) {
     super(mailProvider, eventBus);
   }

   public void starMail(final Mail mail, final boolean star) {

     // optimistic propagation
     if (star) {
       eventBus.post(new MailStaredEvent(mail.getId()));
     } else {
       eventBus.post(new MailUnstaredEvent(mail.getId()));
     }

     mailProvider.starMail(mail.getId(), star)
         .subscribe(new Subscriber<Mail>() {
           @Override public void onCompleted() {
           }

           @Override public void onError(Throwable e) {
             // Oops, something went wrong, "undo"
             if (star) {
               eventBus.post(new MailUnstaredEvent(mail.getId()));
             } else {
               eventBus.post(new MailStaredEvent(mail.getId()));
             }

             if (isViewAttached()) {
               if (star) {
                 getView().showStaringFailed(mail);
               } else {
                 getView().showUnstaringFailed(mail);
               }
             }
           }

           @Override public void onNext(Mail mail) {
           }
         });

     // Note: that we don't cancel this operation in detachView().
     // We want to ensure that this operation finishes
   }

  ...

 }
{% endhighlight %}

 If we star a Mail we fire `MailStaredEvent` and if we unstar a mail we fire a `MailUnstaredEvent`. As you seen in the video by staring a mail the left pane displaying the list of mails gets updated as well since the Presenter is registered on those events. The trick is that we fire the corresponding event before we make the corresponding business logic call and we use the counterpart event to "undo" it in case that that the business logic call fails. We call that **optimistic propagation**. Managing that logic in the Presenter is quite easy as you have seen in the code snipped above. But we also have to update the view. In MVP we have this clear separation between View and business logic, therefore the Presenter is responsible to update the view by calling the corresponding method defined in the view's interface. As you see with a clean MVP architecture and EventBus doing such "complex" things is extremely easy. Presenter is doing the business logic stuff and then the code in your View implementation is just a one-liner. Furthermore, we can use `BaseRxMailPresenter` for `MailsFragment implements BaseMailView` and for `DetailsFragment implements BaseMailView` (displays mail details, the right view of the splitpane view) since `BaseRxMailPresenter` is programs against `BaseMailView`. So no code clones, reuseable and decoupled components thanks to MVP. However, if you would do that all in MailsFragment directly (without MVP), then yes, it might be a little bit more complicated to maintain the correct state.

 Please note that we are not canceling this business logic call in `presenter.detachView()` because we want to ensure that the star / unstar call finishes. You may ask yourself now if that doesn't result in memory leaks during orientation changes. The answer is "it depends on the definition of memory leak". View gets detached from presenter during screen orientation changes. So the huge memory consuming view objects like Activity or Fragment don't leak memory. However, the Presenter creates a new anonymous Subscriber object. Every anonymous object has a hard reference to the surrounding object. So yes, the Presenter can't be garbage collected as long as Subscriber gets unsubscribed (business logic call has finished). But we wouldn't call this a memory leak. It's kind of a temporarily and wanted memory leak and from our point of view completely fine since Presenter is a lightweight object and you know that the presenter can be garbage collected after the business call has finished.

 Another sample where optimistic propagation improves the user experience of your app is when moving a mail from "Inbox" to "Spam":

 <p>
 <iframe width="640" height="480" src="https://www.youtube.com/embed/rA22tsGdeY4?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
 </p>

## Assign Label - MVP for ViewGroup
If you want to avoid Fragments in general you can do that. Mosby offers the same MVP scaffold as for Activities and Fragments for ViewGroups, inclusive ViewState support. The API is the same as for Activity and Fragment. Some default implementation are already available like `MvpViewStateFrameLayout`, `MvpViewStateLinearLayout` and `MvpViewStateRelativeLayout`.
This ViewGroup implementation are also useful if you want to have subviews in a fragment (since you should avoid child fragment). The mail sample app uses that when you want to reassign the label of a mail:

<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/AWt-JhWi3lo?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
</p>

This "Button" for assigning a Label is a `MvpViewStateLinearLayout`. It animates the ProgressBar (loadingView) in and out while loading, uses a `ListPopupWindow` (contentView) to display the list of labels and uses a Toast to inform about errors. If you have a closer look at the video shown above you see that the ViewState is retained during screen orientation changes. By the way: there is no LCE default implementation for ViewGroups (how would you implement that for LinearLayout?).

## User profile - View in View
Most of the time you have one View (and Presenter) filling the whole screen. But that doesn't has to be the case. Typically Fragments are a good candidate for splitting your screen into modular decoupled Views with his own Presenter. The sample mail client implements this as well when you open a profile by clicking on the senders image (avatar):

<p>
<iframe width="640" height="480" src="https://www.youtube.com/embed/4Mce1ljHJz0?rel=0" frameborder="0" class="videoContainer" allowfullscreen></iframe>
</p>

In that sample from the mail app shown in the video above the activity itself is a View having a ViewPager as content view. The Presenter loads a List of `ProfileScreen`. A ProfileScreen is just a POJO.

 {% highlight java %}
 public class ProfileScreen implements Parcelable {

  public static final int TYPE_MAILS = 0;
  public static final int TYPE_ABOUT = 1;

  int type;
  String name;

  private ProfileScreen() {
  }

  public ProfileScreen(int type, String name) {
    this.type = type;
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
{% endhighlight %}

 {% highlight java %}
 public class ProfileScreensAdapter extends FragmentPagerAdapter {


  @Override public Fragment getItem(int position) {

    ProfileScreen screen = screens.get(position);
    if (screen.getType() == ProfileScreen.TYPE_MAILS) {
      return new ProfileMailsFragmentBuilder(person).build();
    }
    if (screen.getType() == ProfileScreen.TYPE_ABOUT) {
      return new AboutFragmentBuilder(person).build();
    }

    throw new RuntimeException("Unknown Profile Screen (no fragment associated with this type");
  }

  @Override public CharSequence getPageTitle(int position) {
    return screens.get(position).getName();
  }
}
{% endhighlight %}


Loading `List<ProfileScreen>` takes 2 seconds (simulates loading the information which screens should be displayed in ViewPager from backend). That's the reason why on activity start you first see a loading spinner in the middle of the screen and then it gets replaced by a ViewPager (and user image). So basically we have LCE (Loading-Content-Error) and therefore we can use Mosby's `MvpLceViewStateActivity`. Next every Fragment in the ViewPager is a MVP View and has it's own Presenter. You see that the Fragments in the ViewPager load data (a loading spinner gets displayed). We guess you get the overall picture: A MVP View can contain independent MVP Views.

## Searching for Mails
In our sample app we can search our inbox for keywords. This is implemented with _pagination_ (displaying a list of mails by loading the whole list in chunks, i.e. display 20 mails and if the user has scrolled to the end of the list then load the next 20 mails). We also want to support screen orientation changes properly. Hence we have to provide a custom `ViewState`.

Basically `SearchView` displays a list of mails, loading and displaying an error view. Therefore we can use a LCE based view state implementation. The question is how do we add the additional state that we are loading more mails (if user has scrolled to the end of the list). In that case it depends a little bit on our implementation. What we did to display "loading more" is we have added an additional ViewType to `SearchResultAdapter` which displays the list of mails matching the search criteria in a RecyclerView (content view). So the last item the RecyclerView displays is a row displaying a `ProgressBar`. Since triggering a "load more action" requires to scroll the content view (RecyclerView) it feels natural to us to make "loading more" part of "showing content. Also is the "load more indicator" part of the RecyclerViews (content view) adapter. Hence we have simply added a boolean flag `loadingMore` as you can see in `SearchViewState`:

{% highlight java %}
// AuthCastedArrayListViewState is a LCE ViewState with an additional "not authenticated" state
public class SearchViewState extends AuthCastedArrayListViewState<List<Mail>, SearchView> {

  boolean loadingMore = false;

  public void setLoadingMore(boolean loadingMore) {
    this.loadingMore = loadingMore;
  }

  @Override public void apply(SearchView view, boolean retained) {

    super.apply(view, retained);

    if (currentViewState == STATE_SHOW_CONTENT) {
      view.showLoadMore(loadingMore);
    }

  }
}
{% endhighlight %}

## Write Mail - MVP & Android Service
Android Services are a fundamental part of android app development. Services are clearly "business logic". Therefore it's obvious that the `Presenter` is responsible to communicate with the Service.
In the mail app we have used an IntentService to send mails. This `SendMailService` is connected over EventBus to the `WritePresenter`:
{% highlight java %}
public class WritePresenter extends MvpBasePresenter<WriteView> {

  private EventBus eventBus;
  private IntentStarter intentStarter;

  public void writeMail(Context context, Mail mail) {
    getView().showLoading();
    intentStarter.sendMailViaService(context, mail);
  }

  public void onEventMainThread(MailSentErrorEvent errorEvent){
    if (isViewAttached()){
      getView().showError(errorEvent.getException());
    }
  }

  public void onEventMainThread(MailSentEvent event){
    if (isViewAttached()){
      getView().finishBecauseSuccessful();
    }
  }
}
{% endhighlight %}

## Main menu - One Model per MVP View
Mosby assumes that every MVP view displays exactly one model (note that displaying a list of items is still displaying one model, the list). Why? Because dealing with different models in the same view increases complexity dramatically and we clearly want to avoid spaghetti code. In the sample app we face that problem in the menu. We haven't refactored that yet to give you the possibility to understand the problem with a concrete code sample. Have a look at the screenshot of the menu:

![Menu]({{ site.baseurl }}/images/mail-menu.png)

The header displays the current authenticated user, represented as `Account`, while the clickable menu items are a `List<Label>` (just ignore statistics). So we have two Models displayed in the same MVP View namely `MenuFragment`. The problem is that it makes your code more complex and harder to maintain if you work with two Models in the same View. Things get even more complex if you decide to use Mosby's ViewState. Because, now you not only have to store the state showing labels, loading labels and error while loading labels, but you also have to store whether the user is authenticate or not.

The solution to this problem is to split the one big MVP View into two views with its own Presenter and own ViewState:

![Menu]({{ site.baseurl }}/images/menu-refactored.jpg)

## Statistics Dialog
Mosby support delegation. The advantage of delegation is that you can add Mosby MVP and ViewState support to any other `Activity`, `Fragment` or `ViewGroup` not included in the Mosby library like `DialogFragment`. In the main menu of the mail example you see the "statistics" menu item. If you click on it a DialogFragment get's displayed. This is implemented like this:

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
