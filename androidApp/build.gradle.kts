import com.ferelin.Libs

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = Libs.Project.currentSDK

    defaultConfig {
        applicationId = "com.ferelin.stockprice"
        minSdk = Libs.Project.minSDK
        targetSdk = Libs.Project.currentSDK
        versionCode = Libs.Project.codeVersion
        versionName = Libs.Project.codeVersionName
    }

    buildFeatures.apply {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.Compose.version
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"true\")")
        }

        debug {
            buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"false\")")
        }
    }
}

dependencies {
    api(project(":shared"))
    implementation(project(":sharedComposables"))

    implementation(Libs.Kotlin.stdLib)
    implementation(Libs.androidCore)
    implementation(Libs.timber)


    implementation(Libs.Coroutines.android)
    implementation(Libs.Koin.android)

    implementation(Libs.material)

    implementation(Libs.Compose.ui)
    implementation(Libs.Compose.util)
    implementation(Libs.Compose.tooling)
    implementation(Libs.Compose.runtime)
    implementation(Libs.Compose.activity)
    implementation(Libs.Compose.navigation)
    implementation(Libs.Compose.animations)
    implementation(Libs.Compose.glide)
    implementation(Libs.Koin.compose)

    implementation(Libs.Accompanist.pager)
    implementation(Libs.Accompanist.insets)
    implementation(Libs.Accompanist.pagerIndicators)

    implementation(platform(Libs.Firebase.platform))
    implementation(Libs.Firebase.analyticsKtx)
    implementation(Libs.Firebase.crashlyticsKtx)
}