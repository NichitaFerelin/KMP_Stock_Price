import com.ferelin.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-parcelize")
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
  api(project(":core:domain"))

  api(Deps.androidLifecycle)
  api(Deps.appCompat)
  api(Deps.browser)
  api(Deps.fragments)
  api(Deps.viewPager)
  api(Deps.constraintLayout)
  api(Deps.material)

  implementation(Deps.glide)
  kapt(Deps.glideCompilerKapt)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}