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

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
  buildFeatures.apply {
    viewBinding = true
  }
  testOptions {
    animationsDisabled = true
  }
}

dependencies {
  implementation(Deps.androidCore)
  implementation(Deps.timber)
  implementation(Deps.workManager)

  implementation(platform(Deps.firebasePlatform))
  implementation(Deps.firebaseCrashlyticsKtx)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  implementation(project(":authentication"))
  implementation(project(":data:data_local"))
  implementation(project(":data:data_network_firebase"))
  implementation(project(":data:data_network_api"))
  implementation(project(":data:data_network_downloader"))
  implementation(project(":core"))

  implementation(project(":features:feature_chart"))
  implementation(project(":features:feature_forecasts"))
  implementation(project(":features:feature_ideas"))
  implementation(project(":features:feature_loading"))
  implementation(project(":features:feature_login"))
  implementation(project(":features:feature_news"))
  implementation(project(":features:feature_profile"))
  implementation(project(":features:feature_search"))
  implementation(project(":features:feature_section_about"))
  implementation(project(":features:feature_section_stocks"))
  implementation(project(":features:feature_stocks_default"))
  implementation(project(":features:feature_stocks_favourite"))
  implementation(project(":features:feature_settings"))

  // For DI
  implementation(Deps.roomRuntime)
  implementation(Deps.retrofit)
  implementation(Deps.firebaseDatabaseKtx)
  implementation(Deps.firebaseAuthenticationKtx)

  // Tests
  androidTestImplementation(Deps.testCoreKtx)
  androidTestImplementation(Deps.testJunitKtx)
  androidTestImplementation(Deps.testRunner)
  androidTestImplementation(Deps.testEspressoCore)
  androidTestImplementation(Deps.testEspressoContrib)
  androidTestImplementation(Deps.testUiAutomator)
  androidTestImplementation(Deps.firebaseDatabaseKtx)
  androidTestImplementation(Deps.firebaseAuthenticationKtx)
}