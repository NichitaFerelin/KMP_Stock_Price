import com.ferelin.Libs

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-parcelize")
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
  api(project(":core"))
}