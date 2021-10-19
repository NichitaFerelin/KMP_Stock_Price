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

    val properties = Properties().apply {
        load(
            FileInputStream(project.rootProject.file("local.properties"))
        )
    }

    buildTypes {

        val PUBLIC_DEBUG_KEY = "c5n906iad3ido15tstu0"

        getByName("debug") {
            resValue(
                "string",
                "api_key",
                (properties["apiKey"] as String?) ?: PUBLIC_DEBUG_KEY
            )
        }
        getByName("release") {
            resValue(
                "string",
                "api_key",
                (properties["apiKey"] as String?) ?: PUBLIC_DEBUG_KEY
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