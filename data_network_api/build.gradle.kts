import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Plugins
import com.ferelin.Projects

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Base.currentSDK

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(Projects.domain))
    implementation(project(Projects.shared))

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