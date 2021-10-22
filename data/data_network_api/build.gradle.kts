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
        val publicDebugKey = "c5n906iad3ido15tstu0"

        debug {
            resValue(
                "string",
                "api_key",
                (properties?.get("apiKey") as String?) ?: publicDebugKey
            )
        }
        release {
            resValue(
                "string",
                "api_key",
                (properties?.get("apiKey") as String?) ?: publicDebugKey
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

    testImplementation(Dependencies.testJunitKtx)
}