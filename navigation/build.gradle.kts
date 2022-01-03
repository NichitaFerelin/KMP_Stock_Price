import com.ferelin.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  compileSdk = Deps.currentSDK
  defaultConfig {
    minSdk = Deps.minSDK
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

dependencies {
  implementation(Deps.fragments)
}