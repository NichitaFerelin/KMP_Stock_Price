# Android Stock Price

## About
An application whose main purpose is to display stock prices. Using the chart, you can analyze the history of price changes. It is possible to add a stock to your favorites list. After that, the price for the selected stock will be displayed in real time using Web Socket (available only if the exchange market is open at the time of viewing). You can log in to the app through your phone number. In this case, all favourite stocks and search requests will be saved in the cloud. 

App API: [Stocks](https://finnhub.io/) | [Crypto](https://nomics.com/docs/)

Google Play: [Stock Price](https://play.google.com/store/apps/details?id=com.ferelin.stockprice)


## Stack
- Clean Architecture try
- MVVM
- Kotlin Coroutines/Flow
- Room
- Dagger2
- Glide
- Data Store Preferences
- Moshi
- Retrofit + okHttp
- Firebase Realtime Db
- Firebase Authorization
- Animations using: MotionLayout, SharedTransitions, PropertyAnimations
- Simple unit tests using: Robolectric & Mockito
- Simple UI tests using: Espresso & UIAutomator 


<p float="middle">
<img src="https://user-images.githubusercontent.com/68856530/117579860-96e90480-b0fd-11eb-8315-695e2adcfae6.gif" height="400" width = "200"/>
<img src="https://user-images.githubusercontent.com/68856530/117579862-98b2c800-b0fd-11eb-9edb-ca65c7b4b2f3.gif" height="400" width = "200"/>
</p>


## Phone numbers for test login
- +16505555555 . Code [123456]
- +16504444444 . Code [123456]
- +16503333333 . Code [123456]


## License

```
   Copyright 2021 Leah Nichita

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
