package com.ferelin

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Libs {

    const val androidCore = "androidx.core:core-ktx:1.7.0"
    const val timber = "com.jakewharton.timber:timber:5.0.1"
    const val material = "com.google.android.material:material:1.5.0"

    object Project {
        const val minSDK = 21
        const val currentSDK = 31
        const val codeVersion = 12
        const val codeVersionName = "4.2.0"
        const val gradlePlugin = "com.android.tools.build:gradle:7.1.3"
    }

    object Kotlin {
        const val version = "1.6.10"
        const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$version"
    }

    object Coroutines {
        const val version = "1.6.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object Compose {
        const val version = "1.1.1"
        const val ui = "androidx.compose.ui:ui:$version"
        const val util = "androidx.compose.ui:ui-util:$version"
        const val material = "androidx.compose.material:material:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val runtime = "androidx.compose.runtime:runtime:$version"
        const val activity = "androidx.activity:activity-compose:$version"
        const val animations = "androidx.compose.animation:animation:$version"
        const val navigation = "androidx.navigation:navigation-compose:2.4.1"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
        const val glide = "com.github.skydoves:landscapist-glide:1.5.0"
        const val lottie = "com.airbnb.android:lottie-compose:5.0.3"
    }

    object Accompanist {
        const val version = "0.23.1"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    }

    object Koin {
        const val version = "3.1.6"
        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
        const val compose = "io.insert-koin:koin-androidx-compose:$version"
    }

    object SqlDelight {
        const val version = "1.5.3"
        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val core = "com.squareup.sqldelight:android-driver:$version"
        const val coroutinesExt = "com.squareup.sqldelight:coroutines-extensions-jvm:$version"
    }

    object Ktor {
        const val version = "2.0.0"
        const val core = "io.ktor:ktor-client-core:$version"
        const val negotiation = "io.ktor:ktor-client-content-negotiation:$version"
        const val jsonSerialization = "io.ktor:ktor-serialization-kotlinx-json:$version"
        const val logging = "io.ktor:ktor-client-logging:$version"
        const val android = "io.ktor:ktor-client-android:$version"
    }

    object Firebase {
        const val platform = "com.google.firebase:firebase-bom:29.3.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val crashlyticsPlugin = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
    }
}