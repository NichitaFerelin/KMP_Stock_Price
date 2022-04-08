import com.ferelin.Libs

plugins {
  kotlin("multiplatform")
  id("com.squareup.sqldelight")
  kotlin("plugin.serialization") version com.ferelin.Libs.Kotlin.version
  id("com.android.library")
}

version = com.ferelin.Libs.Project.codeVersionName

kotlin {
  android()
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(Libs.Coroutines.core)

        implementation(Libs.SqlDelight.core)
        implementation(Libs.SqlDelight.coroutinesExt)

        implementation(Libs.Ktor.core)
        implementation(Libs.Ktor.serialization)
        implementation(Libs.Ktor.logging)

        api(Libs.Koin.core)

        implementation(Libs.serializationJson)
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(Libs.Koin.android)
        implementation(Libs.Ktor.android)
        implementation(Libs.SqlDelight.android)
      }
    }
    val jvmMain by getting {
      dependencies {
        implementation(Libs.SqlDelight.jvm)
        implementation(Libs.Ktor.jvm)
      }
    }
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
sqldelight {
  database("StockPriceDb") {
    packageName = "com.ferelin.stockprice.db"
    sourceFolders = listOf("sqldelight")
  }
}