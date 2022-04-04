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
  buildFeatures.apply {
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion = Libs.Compose.version
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  api(project(":androidApp:core:domain"))

  api(Libs.material)
  api(Libs.browser)

  api(Libs.Compose.ui)
  api(Libs.Compose.util)
  api(Libs.Compose.material)
  api(Libs.Compose.materialIcons)
  api(Libs.Compose.tooling)
  api(Libs.Compose.runtime)
  api(Libs.Compose.activity)
  api(Libs.Compose.navigation)
  api(Libs.Compose.animations)
  api(Libs.Compose.constraintLayout)
  api(Libs.Compose.viewModel)
  api(Libs.Compose.glide)
  api(Libs.Koin.compose)

  api(Libs.Accompanist.insets)
  api(Libs.Accompanist.systemUiController)
  api(Libs.Accompanist.pager)
  api(Libs.Accompanist.pagerIndicators)
}