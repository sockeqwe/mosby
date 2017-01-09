# Sample MVI

This example demonstrates how to use Mosby MVI (Model-View-Intent) to build a reactive app.
In this example we have built a shopping app, where users can put items into a shopping cart.

## Dependencies
The idea of this sample is to use as few dependencies as possible. For instance,
[dagger](https://github.com/google/dagger) has not
been used to make the code understandable by all developers without any prior knowledge of dagger.
So basically this example uses the following libraries:
 - Mobsy MVI (obviously) and RxJava 2 (required by Mosby MVI).
 - Retrofit 2 and Moshi (json parser)
 - Glide (for image loading)