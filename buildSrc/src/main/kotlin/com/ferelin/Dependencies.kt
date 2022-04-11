package com.ferelin

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Libs {

    object Project {
        const val minSDK = 21
        const val currentSDK = 32
        const val codeVersion = 12
        const val codeVersionName = "4.2.0"
    }

    object Plugins {
        const val gradle = "com.android.tools.build:gradle:7.3.0-alpha07"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
        const val google = "com.google.gms:google-services:4.3.10"
        const val firebase = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
    }

    const val androidCore = "androidx.core:core-ktx:1.7.0"
    const val timber = "com.jakewharton.timber:timber:5.0.1"
    const val material = "com.google.android.material:material:1.5.0"
    const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2"

    object Kotlin {
        const val version = "1.6.10"
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Coroutines {
        const val version = "1.6.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object Compose {
        const val version = "1.1.1"
        const val ui = "androidx.compose.ui:ui:$version"
        const val util = "androidx.compose.ui:ui-util:$version"
        const val material = "androidx.compose.material:material:$version"
        const val materialIcons = "androidx.compose.material:material-icons-core:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val runtime = "androidx.compose.runtime:runtime:$version"
        const val activity = "androidx.activity:activity-compose:$version"
        const val animations = "androidx.compose.animation:animation:$version"
        const val navigation = "androidx.navigation:navigation-compose:2.4.1"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
        const val glide = "com.github.skydoves:landscapist-glide:1.5.0"
    }

    object Accompanist {
        const val version = "0.23.1"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    }

    object Koin {
        const val version = "3.1.5"
        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
        const val compose = "io.insert-koin:koin-androidx-compose:$version"
    }

    object SqlDelight {
        const val version = "1.5.3"
        const val core = "com.squareup.sqldelight:runtime:$version"
        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val coroutinesExt = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val android = "com.squareup.sqldelight:android-driver:$version"
        const val jvm = "com.squareup.sqldelight:sqlite-driver:$version"
    }

    object Ktor {
        const val version = "1.6.8"
        const val core = "io.ktor:ktor-client-core:$version"
        const val android = "io.ktor:ktor-client-android:$version"
        const val jvm = "io.ktor:ktor-client-java:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
        const val logging = "io.ktor:ktor-client-logging:$version"
    }

    object Firebase {
        const val platform = "com.google.firebase:firebase-bom:29.3.0"
        const val authenticationKtx = "com.google.firebase:firebase-auth-ktx"
        const val databaseKtx = "com.google.firebase:firebase-database-ktx"
        const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx"
        const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx"
    }

    object Moshi {
        const val version = "1.13.0"
        const val core = "com.squareup.moshi:moshi-kotlin:$version"
        const val compilerKapt = "com.squareup.moshi:moshi-kotlin-codegen:$version"
    }

    object CommonTest {
        const val coreVersion = "1.4.0"
        const val espressoVersion = "3.4.0"
        const val coreKtx = "androidx.test:core-ktx:$coreVersion"
        const val junitKtx = "androidx.test.ext:junit-ktx:1.1.3"
        const val runner = "androidx.test:runner:$coreVersion"
        const val espressoCore = "androidx.test.espresso:espresso-core:$espressoVersion"
        const val espressoContrib = "androidx.test.espresso:espresso-contrib:$espressoVersion"
        const val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
        const val robolectric = "org.robolectric:robolectric:4.6.1"
        const val mockito = "org.mockito:mockito-core:4.0.0"
    }
}