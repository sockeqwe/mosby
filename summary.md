---
layout: page
title: MVP
permalink: /summary/
---

# Summary
_This page summarizes those things that are not discussed in detail on the other pages_

{:toc}


## Modules
Mosby is divided in separated modudules, so that one could include that modules he need.

### Core
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:core:x.x.x'
{% endhighlight %}

This module contains `MosbyActivity` and `MosbyFragment`. Those bases classes already have integrated `Butterknife`, `Icepick` and `FragmentsArgs`. Use this classes if you just want a base Activity or Fragment with just Butterknife included.

### Core Dagger1
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:core-dagger1:x.x.x'
{% endhighlight %}
This module extends the core module and adds support for [Dagger](http://square.github.io/dagger/). It provides `Dagger1MosbyActivity` and `Dagger1MosbyFragment`.

### Mvp
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:mvp:x.x.x'
{% endhighlight %}
This module contains the base interfaces like `MvpView` and `MvpPresenter` and concrete implementations like `MvpBasePresenter`, `MvpNullObjectBasePresenter`, `MvpActivity`, `MvpFragment` and the concrete mvp delegates like `ActivityMvpDelegate`, `FragmentMvpDelegate`. This module also ships support for `ViewGroups` by providing `ViewGroupMvpDelegate`. Furthermore, this module provides default implementations for MvpViews that display a loading indicator (while loading data), a content view that displays the loaded data (i.e. ListView) and a TextView to display a error message if something went wrong. We call this kind of functionality LCE (Loading-Content-Error). You can use `MvpLceActivity` and `MvpLceFragment` if you want a base class that has already implemented the switching from loading view to content view or error view.

### Mvp Dagger1
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:mvp-dagger1:x.x.x'
{% endhighlight %}
This module extends the mvp module and adds to the containing classes support for dependency injection by using Dagger. So instead of `MvpActivity` and `MvpFragment` use `Dagger1MvpActivity` and `Dagger1MvpFragment`.

### ViewState
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:viewstate:x.x.x'
{% endhighlight %}
ViewState is a feature in mosby that allows you to retain the views state during screen orientation changes. This is done by using `ActivityMvpViewStateDelegate`, `FragmentMvpViewStateDelegate` and `ViewGroupMvpDelegate` as the concrete implementations `MvpViewStateActivity`, `MvpViewStateFragment` do. Furthermore, LCE (Loading-Content-Error) implementations like `MvpLceViewStateActivity` and `MvpLceViewStateFragment` are provided by this module.

### ViewState Dagger1
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:viewstate-dagger1:x.x.x'
{% endhighlight %}
As the name already suggests, this module adds Dagger support to the ViewState module by introducing classes like `Dagger1MvpViewStateActivity` and `Dagger1MvpViewStateFragment`.

### Retrofit
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:retrofit:x.x.x'
{% endhighlight %}
This module contains only `LceRetrofitPresenter` which is a Presenter that communicates with your http backend by using [Retrofit](http://square.github.io/retrofit/). Making http requests with Retrofit and `LceRetrofitPresenter` is  a one-liner.

### RxJava
{% highlight groovy %}
	compile 'com.hannesdorfmann.mosby:rx:x.x.x'
{% endhighlight %}
This module provides some base Presenter implementations that can be used to work with [RxJava](https://github.com/ReactiveX/RxJava)
