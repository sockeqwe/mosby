# Mosby
A Model-View-Presenter and Model-View-Intent library for Android apps.


[![Build Status](https://travis-ci.org/sockeqwe/mosby.svg?branch=master)](https://travis-ci.org/sockeqwe/mosby)

# Dependency

```groovy
dependencies {

  compile 'com.hannesdorfmann.mosby3:mvi:3.1.1' // Model-View-Intent
  // or
  compile 'com.hannesdorfmann.mosby3:mvp:3.1.1' // Plain MVP
  // or
  compile 'com.hannesdorfmann.mosby3:viewstate:3.1.1' // MVP + ViewState support
}
```

Additional modules:

```groovy
dependencies {

  // MVP + ViewState + LCE Views
  compile 'com.hannesdorfmann.mosby3:mvp-lce:3.1.1'

  // Null Object Presenter for MVP
  compile 'com.hannesdorfmann.mosby3:mvp-nullobject-presenter:3.1.1'
  
  // Queuing Presenter for MVP
  compile 'com.hannesdorfmann.mosby3:mvp-queuing-presenter:3.1.1'
}
```

**SNAPSHOT:**

```groovy
dependencies {

  compile 'com.hannesdorfmann.mosby3:mvi:3.1.2-SNAPSHOT'

  compile 'com.hannesdorfmann.mosby3:mvp:3.1.2-SNAPSHOT'
  compile 'com.hannesdorfmann.mosby3:viewstate:3.1.2-SNAPSHOT'

  compile 'com.hannesdorfmann.mosby3:mvp-lce:3.1.2-SNAPSHOT'
  compile 'com.hannesdorfmann.mosby3:mvp-nullobject-presenter:3.1.2-SNAPSHOT'
  compile 'com.hannesdorfmann.mosby3:mvp-queuing-presenter:3.1.2-SNAPSHOT'
}
```

You also have to add the url to the snapshot repository:

```gradle
allprojects {
  repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
  }
}
```

# Documentation
See the [project website](http://hannesdorfmann.com/mosby/).

For Model-View-Intent check out [this blog post series](http://hannesdorfmann.com/android/mosby3-mvi-1).

# Changelog
The changelog can be found in the [release section](https://github.com/sockeqwe/mosby/releases)

# Migrating
In Mosby 3.0 we have changed the package name from `com.hannesdorfmann.mosby` to `com.hannesdorfmann.mosby3` (note the **3** at the end).
Migrating a Mosby 2.x based app to Mosby 3.0 should be straightforward:
Just replace all import statements of your app in android studio with `Edit -> Find -> Replace in Path ...`
and set find `import com.hannesdorfmann.mosby` replace with `import com.hannesdorfmann.mosby3`.
There were also some minor API changes (see [changelog](https://github.com/sockeqwe/mosby/releases)),
but most apps should be fine by replacing the import statements.

# Conductor
Mosby has a plugin for [Conductor](https://github.com/bluelinelabs/Conductor). You can find it here: https://github.com/sockeqwe/mosby-conductor

# License
```
Copyright 2015 Hannes Dorfmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
