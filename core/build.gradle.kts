import com.ferelin.Libs

plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  compileSdk = Libs.Project.currentSDK

  defaultConfig {
    minSdk = Libs.Project.minSDK
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

dependencies {
  api(Libs.androidCore)
  api(Libs.documentFile)
  api(Libs.timber)

  api(Libs.Kotlin.stdLib)
  api(Libs.Coroutines.core)
  api(Libs.Coroutines.android)

  api(Libs.Koin.core)
  api(Libs.Koin.android)
}