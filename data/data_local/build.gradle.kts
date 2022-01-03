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
  implementation(project(":domain"))
  implementation(project(":shared"))

  implementation(Deps.kotlinLib)
  implementation(Deps.timber)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  implementation(Deps.moshi)

  implementation(Deps.dataStorePreferences)

  implementation(Deps.roomKtx)
  implementation(Deps.roomRuntime)
  kapt(Deps.roomCompilerKapt)
}