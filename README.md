# ProxyTest

Repository to reproduce and test ClassCastExceptions happening when intercepting kotlin suspending functions returning a Result type with a java Proxy

### How to test

There are 4 testing scenarios that are mixing 2 things:

- Using our Proxy with or without a `yield()` call after handling the result of the function call (check [PluginProviderImpl](app/src/main/java/com/example/proxytest/PluginProviderImpl.kt))
- Using a proguard rule to prevent obfuscation of the Result type or not.

1. Clone this repository
2. Run `make installRelease` to install and launch the MainActivity for the variant that **doesn't obfuscate** the Result type (doesn't include the proguard rule) 
3. Click on both buttons. Wait a couple of seconds. Filter logs with the `ResultApi` tag.
    a. For the "Click here for a Result log" button you will get for every suspending call a proper Result object: `Success(https://picsum.photos/id/1/200/300)`
    b. For the "Click here for a Result log using yield on the proxy" button you will get a ClassCastException crash.
4. Run `make installReleaseWithProguardRule` to install and launch the MainActivity for the variant that **obfuscates** the Result type (Check [proguard-rules.pro](app/proguard-rules.pro))
5. Click on both buttons. Wait a couple of seconds. Filter logs with the `ResultApi` tag.
   a. For the "Click here for a Result log" button you will get a double Wrapped result object: `Success(Success(https://picsum.photos/id/1/200/300))`
   b. For the "Click here for a Result log using yield on the proxy" button you will get a proper Result object: `Success(https://picsum.photos/id/1/200/300)`

