---
layout: page_without_disqus
---

# Model-View-Presenter library for android
_The name of this library, Mosby, has been chosen in honor of Ted Mosby, the architect of the famous tv series How I Met Your Mother. The aim of this library is to help you build modern android apps with a clean Model-View-Presenter architecture. Furthermore, Mosby helps you to handle screen orientation changes by introducing [ViewState]() and retaining Presenters._

## It's a library, not a framework
At first glance Mosby looks a lot like a framework. There are some classes like `MvpFragment` you can extends from, but the point is that you don't have to if you don't want to. At it's core Mosby is a tiny library based on delegation. So you don't have to use `MvpFragment` if you don't want to. You can use delegation and composition to integrate Mosby in your own development stack. Hence you are not caught into a frameworks boundaries and limits.

## Dependencies
Mosby is divided in modules. You can pick those modules you need from the following list of dependencies:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.mosby/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.mosby/core)
{% highlight groovy %}
dependencies {

	compile 'com.hannesdorfmann.mosby:core:x.x.x'
	compile 'com.hannesdorfmann.mosby:core-dagger1:x.x.x'
	compile 'com.hannesdorfmann.mosby:mvp:x.x.x'
	compile 'com.hannesdorfmann.mosby:mvp-dagger1:x.x.x'
	compile 'com.hannesdorfmann.mosby:retrofit:x.x.x'
	compile 'com.hannesdorfmann.mosby:rx:x.x.x'
	compile 'com.hannesdorfmann.mosby:viewstate:x.x.x'
	compile 'com.hannesdorfmann.mosby:viewstate-dagger1:x.x.x'

}
{% endhighlight %}

You need to apply Hugo Visser's awesome [android-apt](https://bitbucket.org/hvisser/android-apt) gradle plugin to run annotation processing
and you have to include the following dependencies:

{% highlight groovy %}
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.android.tools.build:gradle:1.1.3'
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.6'
  }
}

allprojects {
  repositories {
    jcenter()
    maven {
      // For icepick
      url 'https://clojars.org/repo/'
    }
  }
}

dependencies {
  apt 'frankiesardo:icepick-processor:3.0.2'
  apt 'com.hannesdorfmann.fragmentargs:processor:2.1.2'

  // If you want to use dagger1
  apt 'com.squareup.dagger:dagger-compiler:1.2.2'
}
{% endhighlight %}

## Questions
Almost all pages on this site provide a `Disqus` section at the end of the page. Don't hesitate to ask question about the pages content directly in the Disqus section. However, please use the [issue tracker on GitHub](https://github.com/sockeqwe/mosby/issues) to report bugs, issues or start conceptional design discussions. The [changelog](https://github.com/sockeqwe/mosby/releases) can be found in the release section on Github.

## Contributing
If you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request. When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible.

## Proguard
Mosby itself doesn't need any specific proguard rules.
However, third party libraries that are integrated in Mosby like [Butterknife](http://jakewharton.github.io/butterknife/), [FragmentArgs](https://github.com/sockeqwe/fragmentargs), [Icepick](https://github.com/frankiesardo/icepick) and (optional) [dagger 1](http://square.github.io/dagger/) may have there own proguard rules. Please visit these projects site.

## License
{% highlight text %}
 Copyright 2015 Hannes Dorfmann.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
{% endhighlight %}
