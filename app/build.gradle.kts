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
  implementation(project(":navigation"))
  implementation(project(":features:about:ui"))
  implementation(project(":features:authentication:ui"))
  implementation(project(":features:search:ui"))
  implementation(project(":features:settings:ui"))
  implementation(project(":features:splash:ui"))
  implementation(project(":features:stocks:ui"))

  implementation(Deps.androidCore)
  implementation(platform(Deps.firebasePlatform))
  implementation(Deps.firebaseCrashlyticsKtx)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}