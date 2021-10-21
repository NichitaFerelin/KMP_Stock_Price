package com.ferelin

object Dependencies {

    /**
     * Base
     * */
    const val androidCore = "androidx.core:core-ktx:${Versions.androidCore}"
    const val kotlinLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinLib}"
    const val kotlinCoroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    /**
     * UI
     * */
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val fragments = "androidx.fragment:fragment-ktx:${Versions.fragments}"
    const val browser = "androidx.browser:browser:${Versions.browser}"
    const val viewPager = "androidx.viewpager2:viewpager2:${Versions.viewPager}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val material = "com.google.android.material:material:${Versions.material}"

    /**
     * Network
     * */
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"

    /**
     * Room
     * */
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompilerKapt = "androidx.room:room-compiler:${Versions.room}"
    // SPECIAL FOR TESTS
    const val roomTestLiveData =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.roomLiveDataTest}"

    /**
     * Data Store
     * */
    const val dataStorePreferences =
        "androidx.datastore:datastore-preferences:${Versions.dataStorePreferences}"

    /**
     * Dagger
     * */
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompilerKapt = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    /**
     * Firebase
     * */
    const val firebasePlatform = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebaseAnalyticsKtx = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseAuthenticationKtx = "com.google.firebase:firebase-auth-ktx"
    const val firebaseDatabaseKtx = "com.google.firebase:firebase-database-ktx"

    /**
     * Glide
     * */
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompilerKapt = "com.github.bumptech.glide:compiler:${Versions.glide}"

    /**
     * Tests
     * */
    const val testCoreKtx = "androidx.test:core-ktx:${Versions.testCoreKtx}"
    const val testJunitKtx = "androidx.test.ext:junit-ktx:${Versions.testJunitKtx}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val testEspressoCore = "androidx.test.espresso:espresso-core:${Versions.testEspresso}"
    const val testEspressoContrib =
        "androidx.test.espresso:espresso-contrib:${Versions.testEspresso}"
    const val testUiAutomator = "androidx.test.uiautomator:uiautomator:${Versions.testUiAutomator}"
    const val testRobolectric = "org.robolectric:robolectric:${Versions.testRobolectric}"
    const val testMockito = "org.mockito:mockito-core:${Versions.testMockito}"
    const val testCoroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
}