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
  api(project(":core:domain"))

  api(Deps.timber)
  api(Deps.kotlinLib)
  api(Deps.kotlinCoroutines)

  api(platform(Deps.firebasePlatform))
  api(Deps.firebaseDatabaseKtx)
  api(Deps.firebaseAnalyticsKtx)
  api(Deps.firebaseAuthenticationKtx)
  api(Deps.firebaseCrashlyticsKtx)

  api(Deps.retrofit)
  api(Deps.retrofitMoshiConverter)
  api(Deps.okHttp)
  api(Deps.okHttpInterceptor)
  api(Deps.moshi)
  api(Deps.moshiProcessor)

  api(Deps.dataStorePreferences)
  api(Deps.roomKtx)
  api(Deps.roomRuntime)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}