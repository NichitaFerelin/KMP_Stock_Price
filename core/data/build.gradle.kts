import com.ferelin.Libs

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version com.ferelin.Libs.Kotlin.version
}

android {
    compileSdk = Libs.Project.currentSDK

    defaultConfig {
        minSdk = Libs.Project.minSDK
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

    implementation(Libs.Kotlin.serialization)

    api(platform(Libs.Firebase.platform))
    api(Libs.Firebase.crashlyticsKtx)

    api(Libs.Ktor.core)
    api(Libs.Ktor.android)
    api(Libs.Ktor.serialization)
    api(Libs.Ktor.logging)

    api(Libs.dataStorePreferences)

    api(Libs.SqlDelight.core)
    api(Libs.SqlDelight.coroutinesExt)
}
sqldelight {
    database("StockPrice") {
        packageName = "com.ferelin.stockprice"
    }
}