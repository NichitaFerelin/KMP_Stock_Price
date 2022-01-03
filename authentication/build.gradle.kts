import com.ferelin.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
}

android {
  compileSdk = Deps.currentSDK

  defaultConfig {
    minSdk = Deps.minSDK
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
    freeCompilerArgs = freeCompilerArgs +
      ("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
  }
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":shared"))
  implementation(Deps.timber)

  implementation(Deps.kotlinLib)
  implementation(Deps.kotlinCoroutines)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  implementation(platform(Deps.firebasePlatform))
  implementation(Deps.firebaseAuthenticationKtx)
}