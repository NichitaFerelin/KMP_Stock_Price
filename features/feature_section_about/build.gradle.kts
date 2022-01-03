import com.ferelin.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-kapt")
}

android {
  compileSdk = Deps.currentSDK
  defaultConfig {
    minSdk = Deps.minSDK
  }
  buildFeatures.apply {
    viewBinding = true
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

dependencies {
  implementation(project(":core"))

  implementation(project(":features:feature_profile"))
  implementation(project(":features:feature_chart"))
  implementation(project(":features:feature_news"))
  implementation(project(":features:feature_forecasts"))
  implementation(project(":features:feature_ideas"))

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}