---
layout: page
title: MVP
permalink: /summary/
---

# Summary - FAQ
_This page summarizes those things that are not discussed in detail on the other pages_

## Decision chart
Mosby provides some modules and some classes that one can use as base class to extend from. Here is a little decision chart that you may find helpful to chose the correct base class:

![Decision chart]({{ site.baseurl }}/images/decision.png)

## Which Presenter implementation should I extend from to create my own Presenter?
`MvpPresenter` is on top of the inheritance hierarchy. It's an interface, hence you can write your own Presenter easily. However, there are already two implementation of `MvpPresenter` you can extend from:
 - `MvpBasePresenter`: Internally uses a `WeakReference` to the attached View to avoid memory leaks. However, that leads to another problem: possible null reference to the view if the view is not attached. Hence you always have to check with `isViewAttached()` (or check `getView() != null` which is equivalent) if a view is attached to the Presenter before invoking a view's method.
 - `MvpNullObjectBasePresenter`: To avoid all this `isViewAttached()` checks one could use this Presenter class which implements the [Null Object Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern). That means that there is always a view attached to this kind of Presenter. The attached view is either the "real" view (like an Activity or Fragment) or a fake view (null object) constructed by using reflections that simply does nothing if you invoke an method on it.

## Can the Presenter and its view be out of sync during a screen orientation change?
Excellent question. Mosby assumes that all interaction from Presenter with the View happens on android's main UI thread. Hence the answer is **no** that cannot happen since screen orientation changes are executed on the main UI thread as well. So either is a screen orientation executed completely (view reattached) or the presenter invokes the views method after view is reattached since both run on main UI thread or the presenter invokes the views methods before starting screen orientation change.

## Why do I need `isViewAttached()` check in MvpBasePresenter
In a perfect world you wouldn't need this check. As already said, in Mosby all interaction from Presenter to View happens on android's main UI thread. Since screen orientation changes are also executed on the main UI thread it is not possible to run into the scenario where view is detached while presenter still wants to update the UI, right? However, we are not living in a perfect world. Let's say you use an AsyncTask to make an http request and the Presenter gets the result back and updates the View. If you forget to cancel this AsyncTask after the View has been destroyed (i.e. your apps user press the back button, `presenter.detachView(false)` will be called) then you can run into the scenario that the View is detached from Presenter and then the Presenter gets the result back from AsyncTask and wants to update the View which is null (because detached). So the `isViewAttached()` check is basically some kind of safety net. In a perfect implementation you wouldn't need it (presenter - view interaction on main thread, cancel all async background task when view gets destroyed etc.), but in general I would say it wouldn't hurt to drive with a safety net.

## Why does MvpBasePresenter uses WeakReference for View reference
Basically there is no need to use a `WeakReference` since Presenter's reference to the View will be set to null in `presenter.detachView()` and attached in `presenter.attachView()`. But in Mosby you can create your own `MvpDelegate` to change Mosby's default implementation. So we have decided to use a WeakReference to avoid memory leaks if you use you use a custom MvpDelegate that is not implementing the contract of attaching and detaching View from presenter properly. So again, this is just a safety net and is not required in a perfect world. If you know what your doing and are sure that your code runs in a "perfect world" then it's completely fine to write your own Presenter that is not based on WeakReference and doesn't need a `isViewAttached()` check. Please note that `MvpPresenter` is an interface, so you can implement your own Presenters easily.

## Where do I get more information about "PresentationModel" mentioned in the "MVP basics" section?
Have a look [here](https://github.com/sockeqwe/mosby/issues/85)

## Modules
Mosby is divided in separated modules so that you can include only that modules you really need. Here is an overview which module provides which classes and what functionality.

### Mvp
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:mvp:x.x.x'
{% endhighlight %}
This module contains the base interfaces like `MvpView` and `MvpPresenter` and concrete implementations like `MvpBasePresenter`, `MvpNullObjectBasePresenter`, `MvpActivity`, `MvpFragment` and the concrete mvp delegates like `ActivityMvpDelegate`, `FragmentMvpDelegate`. This module also ships support for `ViewGroups` by providing `ViewGroupMvpDelegate`. Furthermore, this module provides default implementations for MvpViews that display a loading indicator (while loading data), a content view that displays the loaded data (i.e. ListView) and a TextView to display a error message if something went wrong. We call this kind of functionality LCE (Loading-Content-Error). You can use `MvpLceActivity` and `MvpLceFragment` if you want a base class that has already implemented the switching from loading view to content view or error view.

### ViewState
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:viewstate:x.x.x'
{% endhighlight %}
ViewState is a feature in mosby that allows you to retain the views state during screen orientation changes. This is done by using `ActivityMvpViewStateDelegate`, `FragmentMvpViewStateDelegate` and `ViewGroupMvpDelegate` as the concrete implementations `MvpViewStateActivity`, `MvpViewStateFragment` do. Furthermore, LCE (Loading-Content-Error) implementations like `MvpLceViewStateActivity` and `MvpLceViewStateFragment` are provided by this module.

## Additional resources
 - [Ted Mosby - Software Architect:](http://hannesdorfmann.com/android/mosby/) The original blog post where Mosby has been introduced.
 - [Stinsons Playbook for Mosby:](http://hannesdorfmann.com/android/mosby-playbook/) A blog post providing some tips you may find useful while creating Mosby powered apps.
