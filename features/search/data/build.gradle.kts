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
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

dependencies {
  implementation(project(":core:data"))
  implementation(project(":features:search:domain"))

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  implementation(Deps.roomKtx)
  implementation(Deps.roomRuntime)
  kapt(Deps.roomCompilerKapt)
}