import com.ferelin.Deps
import java.io.FileInputStream
import java.util.*

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

  val properties: Properties? = try {
    Properties().apply {
      load(
        FileInputStream(project.file("local.properties"))
      )
    }
  } catch (e: Exception) {
    null
  }

  buildTypes {
    val publicFinnhubDebugToken = "c5n906iad3ido15tstu0"
    val publicNomicsDebugToken = "cb99d1ebf28482d6fb54f7c9002319aea14401c7"

    debug {
      resValue(
        "string",
        "api_finnhub_token",
        (properties?.get("apiFinnhubToken") as String?) ?: publicFinnhubDebugToken
      )
      resValue(
        "string",
        "api_nomics_token",
        (properties?.get("apiNomicsToken") as String?) ?: publicNomicsDebugToken
      )
    }
    release {
      resValue(
        "string",
        "api_finnhub_token",
        (properties?.get("apiFinnhubToken") as String?) ?: publicFinnhubDebugToken
      )
      resValue(
        "string",
        "api_nomics_token",
        (properties?.get("apiNomicsToken") as String?) ?: publicNomicsDebugToken
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
  implementation(project(":domain"))
  implementation(project(":shared"))

  implementation(Deps.timber)

  implementation(Deps.kotlinLib)
  implementation(Deps.kotlinCoroutines)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  implementation(Deps.retrofit)
  implementation(Deps.retrofitMoshiConverter)
  implementation(Deps.okHttp)
  implementation(Deps.okHttpInterceptor)
  implementation(Deps.moshi)
}