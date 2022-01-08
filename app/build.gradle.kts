import com.ferelin.Deps

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
  id("com.github.ben-manes.versions")
  id("com.google.firebase.crashlytics")
}

android {
  compileSdk = Deps.currentSDK

  defaultConfig {
    applicationId = "com.ferelin.stockprice"
    minSdk = Deps.minSDK
    targetSdk = Deps.currentSDK
    versionCode = 12
    versionName = "4.2.0"
  }
  buildFeatures.apply {
    viewBinding = true
  }
  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
}

dependencies {
  implementation(project(":core"))
  implementation(project(":core:ui"))
  implementation(project(":core:domain"))
  implementation(project(":core:data"))
  implementation(project(":navigation"))
  implementation(project(":features:about"))
  implementation(project(":features:authentication"))
  implementation(project(":features:search"))
  implementation(project(":features:settings"))
  implementation(project(":features:splash"))
  implementation(project(":features:stocks"))

  implementation(Deps.androidCore)
  implementation(platform(Deps.firebasePlatform))
  implementation(Deps.firebaseCrashlyticsKtx)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}