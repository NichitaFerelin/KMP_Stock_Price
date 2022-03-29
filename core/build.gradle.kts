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
  api(Deps.androidCore)
  api(Deps.documentFile)
  api(Deps.timber)
  api(Deps.kotlinLib)
  api(Deps.kotlinCoroutinesCore)
  api(Deps.kotlinCoroutines)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}