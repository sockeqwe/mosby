# Mail sample
This sample mimics a mail client. It's not a real mail client, there is no POP3 or IMAP Server behind it.
All the data is randomly generated on app start. There is no persistent layer like local database on the device. The APK file can be downloaded from [here](https://github.com/sockeqwe/mosby/releases/download/1.1.0/sample-mail.apk).

**Important:** This sample project uses annotation processors. If you import this sample into android studio, you may have to run `Build --> Rebuild Project` manually to ensure that all annotation processors have generated code.


## Inheritance Hierarchy
The package `com.hannesdorfmann.mosby.sample.mail.base` contains some base presenter classes. Furthermore some base activities and fragments classes are provided in that package that setup `Butterknife`, `Icepick` and `FragmentArgs`.


## UI
This app is not a reference app for material design nor do I have time to keep the latest support library feartures and so on and so forth. This sample demonstrates how to use Mosby. However, feel free to make a pull request to improve this sample app.