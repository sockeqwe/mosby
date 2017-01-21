# Mosby
A Model-View-Presenter library for modern Android apps.

Check the [project website](http://hannesdorfmann.com/mosby/) for more information.

[![Build Status](https://travis-ci.org/sockeqwe/mosby.svg?branch=master)](https://travis-ci.org/sockeqwe/mosby)

# Dependency
```groovy
dependencies {
	compile 'com.hannesdorfmann.mosby:mvp:2.0.1'
	compile 'com.hannesdorfmann.mosby:viewstate:2.0.1' // optional viewstate feature
}
```

Mosby3 preview:
```groovy
dependencies {
	compile 'com.hannesdorfmann.mosby3:mvp:3.0.0-alpha2'
	compile 'com.hannesdorfmann.mosby3:viewstate:3.0.0-alpha2'
	compile 'com.hannesdorfmann.mosby3:mvi:3.0.0-alpha2'
}
```

SNAPSHOT:
```groovy
dependencies {
	compile 'com.hannesdorfmann.mosby3:mvp:3.0.0-SNAPSHOT'
	compile 'com.hannesdorfmann.mosby3:viewstate:3.0.0-SNAPSHOT'
	compile 'com.hannesdorfmann.mosby3:mvi:3.0.0-SNAPSHOT'
}
```


You also have to add the url to the snapshot repository:

```gradle
allprojects {
  repositories {
    ...

    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}
```


# Sample APK
You can find the sample apk files in the [release section](https://github.com/sockeqwe/mosby/releases)

# Changelog
The changelog can be found in the [release section](https://github.com/sockeqwe/mosby/releases)

# Migrating
See the [project website](http://hannesdorfmann.com/mosby/) for migrating from Mosby 1.x to Mosby 2.0

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
