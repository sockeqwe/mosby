---
layout: page_without_disqus
---

# Model-View-Presenter library for android
The name of this library _Mosby_ has been chosen in honor of Ted Mosby, the architect of the famos tv series _How I Met Your Mother_. The aim of this library is to help you build modern android apps with a clean Model-View-Presenter architecture. Furthermore, Mosby helps you to handle screen orientation changes by introducing [ViewState]() and retaining Presenters.

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
  apt 'com.hannesdorfmann.fragmentargs:processor:2.1.0'

  // If you want to use dagger1
  apt 'com.squareup.dagger:dagger-compiler:1.2.2'
}
{% endhighlight %}

## Questions
Almost all pages on this site provide a `disqus` section at the end of the page. Don't hesitate to ask question about the pages content directly in the disqus section. To report bugs, issues or start conceptional design discussions please use the [issue tracker on GitHub](https://github.com/sockeqwe/mosby/issues). The [changelog](https://github.com/sockeqwe/mosby/releases) can be found in the release section on Github.

## Contributing
If you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request. When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible.

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
