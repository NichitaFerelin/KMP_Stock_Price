/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.dependencies

object Dependencies {

    /**
     * Base
     * */
    const val androidCore = "androidx.core:core-ktx:${Versions.androidCore}"
    const val kotlinLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinLib}"
    const val kotlinCoroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"

    /**
     * Classpath
     * */
    const val gradleTools = "com.android.tools.build:gradle:${Versions.gradleTools}"
    const val gradleKotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinLib}"
    const val googleServices = "com.google.gms:google-services:${Versions.googleServices}"

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
     * Compose
     * */
    const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    const val composeRuntimeLivedata =
        "androidx.compose.runtime:runtime-livedata:${Versions.compose}"

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