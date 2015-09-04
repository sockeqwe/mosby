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
