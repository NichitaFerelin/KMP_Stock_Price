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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
}

dependencies {
    implementation(project(":core:domain"))

    implementation(Libs.Kotlin.serialization)

    implementation(Libs.Ktor.core)
    implementation(Libs.Ktor.negotiation)
    implementation(Libs.Ktor.jsonSerialization)
    implementation(Libs.Ktor.logging)
    implementation(Libs.Ktor.android)

    implementation(Libs.SqlDelight.core)
    implementation(Libs.SqlDelight.coroutinesExt)
}
sqldelight {
    database("StockPrice") {
        packageName = "com.ferelin.stockprice.db"
    }
}