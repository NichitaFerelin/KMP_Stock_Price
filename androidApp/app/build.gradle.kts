import com.ferelin.Libs

plugins {
  id("com.android.application")
  id("kotlin-android")
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
  implementation(project(":shared"))
  implementation(project(":androidApp:core"))
  implementation(project(":androidApp:core:ui"))
  implementation(project(":androidApp:core:domain"))
  implementation(project(":androidApp:core:data"))
  implementation(project(":androidApp:features:splash"))
  implementation(project(":androidApp:features:home"))
  implementation(project(":androidApp:features:search"))
  implementation(project(":androidApp:features:settings"))
  implementation(project(":androidApp:features:about"))
  implementation(project(":androidApp:features:login"))

  implementation(platform(Libs.Firebase.platform))
  implementation(Libs.Firebase.crashlyticsKtx)
}