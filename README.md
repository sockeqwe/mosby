# Mosby
A Model-View-Presenter library for modern Android apps.

Read more about mosby in [my blog post](http://hannesdorfmann.com/android/mosby/)

# Dependency
Latest version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.mosby/core/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.mosby/core)

Pick the module(s) you want to use:
```groovy
dependencies {

	compile 'com.hannesdorfmann.mosby:core:x.x.x'
	compile 'com.hannesdorfmann.mosby:core-dagger1:x.x.x'
	compile 'com.hannesdorfmann.mosby:mvp:x.x.x'
	compile 'com.hannesdorfmann.mosby:mvp-dagger1:x.x.x'
	compile 'com.hannesdorfmann.mosby:retrofit:x.x.x'
	compile 'com.hannesdorfmann.mosby:rx:x.x.x'
	compile 'com.hannesdorfmann.mosby:viewstate:x.x.x'
	compile 'com.hannesdorfmann.mosby:viewstate-dagger1:x.x.x'

	testCompile 'com.hannesdorfmann.mosby:testing:x.x.x'

}
```

where you have to replace x.x.x with the latest version.


This library uses annotation processing libraries.
You need to apply Hugo Visser's awesome [android-apt](https://bitbucket.org/hvisser/android-apt) gradle plugin to run annotation processing
and you have to include the following dependencies:

```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.android.tools.build:gradle:1.1.3'
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
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
```

# Sample APK
You can find the smaple apk files in the [release section](https://github.com/sockeqwe/mosby/releases)

# Changelog
The changelog can be found in the [release section](https://github.com/sockeqwe/mosby/releases)

# Proguard
Mosby itself doesn't need any specific proguard rules.
However, third party libraries that are integrated in mosby like [Butterknife](http://jakewharton.github.io/butterknife/), [FragmentArgs](https://github.com/sockeqwe/fragmentargs), [Icepick](https://github.com/frankiesardo/icepick) and (optional) [dagger 1](http://square.github.io/dagger/) may have there own proguard rules. Please visit these projects site.
