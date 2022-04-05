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
        api(Libs.Coroutines.core)

        api(Libs.SqlDelight.core)
        api(Libs.SqlDelight.coroutinesExt)

        api(Libs.Ktor.core)
        api(Libs.Ktor.serialization)
        api(Libs.Ktor.logging)

        api(Libs.Koin.core)

        api(Libs.serializationJson)
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(Libs.Koin.android)
        implementation(Libs.Ktor.android)
        implementation(Libs.SqlDelight.android)
      }
    }
    val jvmMain by getting
  }
}
android {
  compileSdk = Libs.Project.currentSDK

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].resources.srcDirs("src/commonMain/assets")

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