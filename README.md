# Android Stock Price

## About
Application allow you to monitor for stock price changes in real time. Also you can analyze the company stock history by chart.


App API: **finnhub**. 
Requests limit: 1/sec [ThrottleManager.kt at Remote module]
## Stack
- Kotlin Coroutines/Flow
- Room
- Data Store Preferences
- Moshi
- Retrofit + okHttp
- Simple screen with JetpackCompose [WelcomeFragment.kt at JetpackCompose branch]
- Some Unit tests with Robolectric and Mockito
## Visual architecture guide
![Alt Text](https://user-images.githubusercontent.com/68856530/112752494-0be3fd00-8fdc-11eb-8c54-d0c3412e44e7.png)
