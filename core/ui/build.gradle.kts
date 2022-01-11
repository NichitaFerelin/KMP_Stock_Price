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
    compose = true
    viewBinding = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion  = Deps.composeVersion
  }
  kotlinOptions {
    jvmTarget = "1.8"
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

  api(Deps.composeUi)
  api(Deps.composeUtil)
  api(Deps.composeMaterial)
  api(Deps.composeMaterialIcons)
  api(Deps.composeTooling)
  api(Deps.composeRuntime)

  api(Deps.accompanistInsets)
  api(Deps.accompanistSystemUiController)
  api(Deps.accompanistPager)
  api(Deps.accompanistPagerIndicators)

  implementation(Deps.glide)
  kapt(Deps.glideCompilerKapt)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}