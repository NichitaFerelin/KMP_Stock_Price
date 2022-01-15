package com.ferelin

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Deps {
  const val minSDK = 21
  const val currentSDK = 31

  const val kotlinVersion = "1.6.0"
  const val kotlinCoroutinesVersion = "1.5.2"

  const val androidCore = "androidx.core:core-ktx:1.7.0"
  const val kotlinLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
  const val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion"
  const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion"
  const val timber = "com.jakewharton.timber:timber:5.0.1"

  const val androidLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0"
  const val appCompat = "androidx.appcompat:appcompat:1.4.0"
  const val fragments = "androidx.fragment:fragment-ktx:1.4.0"
  const val browser = "androidx.browser:browser:1.4.0"
  const val viewPager = "androidx.viewpager2:viewpager2:1.0.0"
  const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.2"
  const val material = "com.google.android.material:material:1.5.0-rc01"
  const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0"

  const val composeVersion = "1.1.0-rc01"
  const val composeUi = "androidx.compose.ui:ui:$composeVersion"
  const val composeUtil = "androidx.compose.ui:ui-util:$composeVersion"
  const val composeMaterial = "androidx.compose.material:material:$composeVersion"
  const val composeMaterialIcons = "androidx.compose.material:material-icons-core:$composeVersion"
  const val composeTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
  const val composeRuntime = "androidx.compose.runtime:runtime:$composeVersion"
  const val composeActivity = "androidx.activity:activity-compose:$composeVersion"
  const val composeNavigation = "androidx.navigation:navigation-compose:2.4.0-rc01"

  private const val accompanistVersion = "0.22.0-rc"
  const val accompanistInsets = "com.google.accompanist:accompanist-insets:$accompanistVersion"
  const val accompanistSystemUiController = "com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion"
  const val accompanistPager = "com.google.accompanist:accompanist-pager:$accompanistVersion"
  const val accompanistPagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$accompanistVersion"
  const val accompanistSwipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$accompanistVersion"
  const val accompanistFlowLayout = "com.google.accompanist:accompanist-flowlayout:$accompanistVersion"

  private const val daggerVersion = "2.40.5"
  const val dagger = "com.google.dagger:dagger:$daggerVersion"
  const val daggerCompilerKapt = "com.google.dagger:dagger-compiler:$daggerVersion"

  const val glideCompose = "com.github.skydoves:landscapist-glide:1.4.5"

  const val roomVersion = "2.4.0"
  const val roomRuntime = "androidx.room:room-runtime:$roomVersion"
  const val roomKtx = "androidx.room:room-ktx:$roomVersion"
  const val roomCompilerKapt = "androidx.room:room-compiler:$roomVersion"

  const val documentFile = "androidx.documentfile:documentfile:1.0.1"
  const val dataStorePreferences = "androidx.datastore:datastore-preferences:1.0.0"

  private const val retrofitVersion = "2.9.0"
  const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
  const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"

  private const val okHttpVersion = "4.9.1"
  const val okHttp = "com.squareup.okhttp3:okhttp:$okHttpVersion"
  const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"

  private const val moshiVersion = "1.13.0"
  const val moshi = "com.squareup.moshi:moshi-kotlin:$moshiVersion"
  const val moshiProcessor = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"

  const val firebasePlatform = "com.google.firebase:firebase-bom:29.0.3"
  const val firebaseAnalyticsKtx = "com.google.firebase:firebase-analytics-ktx"
  const val firebaseCrashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx"
  const val firebaseAuthenticationKtx = "com.google.firebase:firebase-auth-ktx"
  const val firebaseDatabaseKtx = "com.google.firebase:firebase-database-ktx"

  private const val testCoreVersion = "1.4.0"
  private const val testEspressoVersion = "3.4.0"
  const val testCoreKtx = "androidx.test:core-ktx:$testCoreVersion"
  const val testJunitKtx = "androidx.test.ext:junit-ktx:1.1.3"
  const val testRunner = "androidx.test:runner:$testCoreVersion"
  const val testEspressoCore = "androidx.test.espresso:espresso-core:$testEspressoVersion"
  const val testEspressoContrib = "androidx.test.espresso:espresso-contrib:$testEspressoVersion"
  const val testUiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
  const val testRobolectric = "org.robolectric:robolectric:4.6.1"
  const val testMockito = "org.mockito:mockito-core:4.0.0"
  const val testCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion"
  const val roomTestLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:$roomVersion"
}