---
layout: page_without_disqus
---

# Model-View-Presenter library for android
_The name of this library, Mosby, has been chosen in honor of Ted Mosby, the architect of the famous tv series How I Met Your Mother. The aim of this library is to help you build modern android apps with a clean Model-View-Presenter architecture. Furthermore, Mosby helps you to handle screen orientation changes by introducing [ViewState](http://hannesdorfmann.com/mosby/viewstate/) and retaining Presenters._

## It's a library, not a framework
At first glance Mosby looks a lot like a framework. There are some classes like `MvpFragment` you can extends from, but the point is that you don't have to if you don't want to. At it's core Mosby is a tiny library based on delegation. So you don't have to use `MvpFragment` if you don't want to. You can use delegation and composition to integrate Mosby in your own development stack. Hence you are not caught into a frameworks boundaries and limits.

## Getting started
In the [getting started](http://hannesdorfmann.com/mosby/getting-started/) section you will find some simple examples how to use Mosby to build MVP based screens. Mosby also supports Kotlin and you will find there a simple Kotlin example as well.
If you are looking for a real app powered by Mosby, you should have a look at the [First App section](http://hannesdorfmann.com/mosby/first-app/) where you will find a complex E-Mail client app.

## Dependencies
Mosby is divided in modules. You can pick those modules you need from the following list of dependencies:


<div class="highlight"><pre><code class="groovy"><span class="n">dependencies</span> <span class="o">{</span>
	<span class="n">compile</span> <span class="s1">'com.hannesdorfmann.mosby:mvp:<span class="mosbyVersion">2.0.0</span>'</span>
	<span class="n">compile</span> <span class="s1">'com.hannesdorfmann.mosby:viewstate:<span class="mosbyVersion">2.0.0</span>'</span>
<span class="o">}</span></code></pre></div>

## Migration to Mosby 2.0
With Mosby 2.0 all third party dependencies and libraries like `Butterknife` and `FragmentArgs` has been removed. That also means that many modules from Mosby 1.0 has been removed as well like `core` (including MosbyActivity and MosbyFragment), `dagger1 modules`, `retrofit` and `rx`.
The reason is that third party dependencies bring a lot of problems and they shouldn't never be part of Mosby. A concrete example was Butterknife integration in Mosby. Mosby 1.1.0 used Butterknife 6 with `Butterknife.inject()` used in `MosbyActivity`. Then Butterknife 7 was released with breaking API changes like `Butterknife.bind()` was introduced to replace `Butterknife.inject()`. So we had migrate Mosby to Butterknife 7 and released Mosby 1.2.0. The problem was that every app out there using Mosby had to migrate the whole app to Butterknife 7 as well in order to upgrade to Mosby 1.2.0 and any future Mosby release that may containing bugfixes and improvements. That can happen with any third party library at any time like upgrading from retrofit 1.9.0 to retrofit 2.0.0. That's the reason why we have removed all third party libraries and Mosby modules depending on them other developers may have find useful like dagger1 integration. We recommend you (or your team) to build such "base classes" for each app so you can decide per app which third party libraries you want to use and when to upgrade these libraries to a newer available version. You can checkout the [v1.x branch](https://github.com/sockeqwe/mosby/tree/v1.x) and copy manually those classes your need into your apps repository. We apologize for any inconvenience this may cause but we are sure that this help us to avoid problems in the future and to focus on the fact that Mosby is a MVP library and not a bloated framework.

## Questions
Almost all pages on this site provide a `Disqus` section at the end of the page. Don't hesitate to ask question about the pages content directly in the Disqus section. However, please use the [issue tracker on GitHub](https://github.com/sockeqwe/mosby/issues) to report bugs, issues or start conceptional design discussions. The [changelog](https://github.com/sockeqwe/mosby/releases) can be found in the release section on Github.

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


<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script>
(function() {
  var mavenApi = "http://search.maven.org/solrsearch/select?q=g:%22com.hannesdorfmann.mosby%22+AND+a:%22mvp%22";
  $.getJSON(mavenApi)
    .done(function( data ) {
			var version = data.response.response.docs[0].latestVersion;
			$('.mosbyVersion').text(version);
    });
})();
</script
