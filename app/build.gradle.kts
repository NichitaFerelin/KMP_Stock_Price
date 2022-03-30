import com.ferelin.Libs

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
  id("com.google.firebase.crashlytics")
}

android {
  compileSdk = Libs.Project.currentSDK

  defaultConfig {
    applicationId = "com.ferelin.stockprice"
    minSdk = Libs.Project.minSDK
    targetSdk = Libs.Project.currentSDK
    versionCode = Libs.Project.codeVersion
    versionName = Libs.Project.codeVersionName
  }

  buildFeatures.apply {
    compose = true
    buildConfig = true
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

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"true\")")
    }

    debug {
      buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"false\")")
    }
  }
}

dependencies {
  implementation(project(":core"))
  implementation(project(":core:ui"))
  implementation(project(":core:domain"))
  implementation(project(":core:data"))
  implementation(project(":features:splash"))
  implementation(project(":features:home"))
  implementation(project(":features:search"))
  implementation(project(":features:settings"))
  implementation(project(":features:about"))
  implementation(project(":features:login"))

  implementation(platform(Libs.Firebase.platform))
  implementation(Libs.Firebase.crashlyticsKtx)

  implementation(Libs.Dagger.core)
  kapt(Libs.Dagger.compilerKapt)
}