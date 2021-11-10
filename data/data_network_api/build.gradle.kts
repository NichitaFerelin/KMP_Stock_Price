import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Projects
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Base.currentSDK

    val properties: Properties? = try {
        // project.rootProject.file -> Cannot access class java.io.File
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
    implementation(project(Projects.domain))
    implementation(project(Projects.shared))

    implementation(Dependencies.timber)

    implementation(Dependencies.kotlinLib)
    implementation(Dependencies.kotlinCoroutines)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)

    implementation(Dependencies.retrofit)
    implementation(Dependencies.retrofitMoshiConverter)
    implementation(Dependencies.okHttp)
    implementation(Dependencies.okHttpInterceptor)
    implementation(Dependencies.moshi)
}