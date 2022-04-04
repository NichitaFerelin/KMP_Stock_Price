import com.ferelin.Libs

plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

kotlin {
  android()
  jvm()

  sourceSets {
    val commonMain by getting
    val androidMain by getting
    val jvmMain by getting
  }
}
android {
  compileSdk = Libs.Project.currentSDK
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = Libs.Project.minSDK
    targetSdk = Libs.Project.currentSDK
  }
}