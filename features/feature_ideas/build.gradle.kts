plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-kapt")
}

android {
  compileSdk = com.ferelin.Deps.currentSDK
  defaultConfig {
    minSdk = com.ferelin.Deps.minSDK
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
}