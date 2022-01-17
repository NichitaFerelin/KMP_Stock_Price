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

  buildTypes {
    val publicFinnhubDebugToken = "c5n906iad3ido15tstu0"
    val publicNomicsDebugToken = "cb99d1ebf28482d6fb54f7c9002319aea14401c7"

    debug {
      resValue(
        "string",
        "api_finnhub_token",
        (properties["apiFinnhubToken"] as String?) ?: publicFinnhubDebugToken
      )
      resValue(
        "string",
        "api_nomics_token",
        (properties["apiNomicsToken"] as String?) ?: publicNomicsDebugToken
      )
    }
    release {
      resValue(
        "string",
        "api_finnhub_token",
        (properties["apiFinnhubToken"] as String?) ?: publicFinnhubDebugToken
      )
      resValue(
        "string",
        "api_nomics_token",
        (properties["apiNomicsToken"] as String?) ?: publicNomicsDebugToken
      )
    }
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
    freeCompilerArgs = freeCompilerArgs +
      ("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
  }
}

dependencies {
  api(project(":core:domain"))

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
  kapt(Deps.moshiProcessor)

  api(Deps.dataStorePreferences)

  api(Deps.roomKtx)
  api(Deps.roomRuntime)
  kapt(Deps.roomCompilerKapt)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}