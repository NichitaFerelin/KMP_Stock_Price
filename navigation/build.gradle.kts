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
  implementation(project(":core:ui"))
  implementation(project(":features:search"))
  implementation(project(":features:about"))
  implementation(project(":features:authentication"))
  implementation(project(":features:settings"))
  implementation(project(":features:splash"))
  implementation(project(":features:stocks"))

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}